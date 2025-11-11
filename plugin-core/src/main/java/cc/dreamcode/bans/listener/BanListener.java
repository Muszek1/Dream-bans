package cc.dreamcode.bans.listener;

import static cc.dreamcode.utilities.bukkit.StringColorUtil.fixColor;

import cc.dreamcode.bans.config.MessageConfig;
import cc.dreamcode.bans.config.PluginConfig;
import cc.dreamcode.bans.profile.Profile;
import cc.dreamcode.bans.profile.ProfileService;
import cc.dreamcode.utilities.DateUtil;
import eu.okaeri.injector.annotation.Inject;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletionException;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class BanListener implements Listener {

  private final ProfileService profileService;
  private final MessageConfig messageConfig;
  private final PluginConfig pluginConfig;

  @EventHandler
  public void onPreLogin(AsyncPlayerPreLoginEvent event) {
    final String name = event.getName();
    final UUID uuid = event.getUniqueId();
    final String ip = event.getAddress().getHostAddress();

    if (this.pluginConfig.blacklistPlayers.stream()
        .anyMatch(player -> player.equalsIgnoreCase(name))) {

      String kickMsg = fixColor(this.messageConfig.blacklistKick);

      event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, kickMsg);
      return;
    }

    try {
      Profile profile = this.profileService.loadAsync(uuid, name).join();

      profile.setLastIp(ip);
      profile.save();

      long now = Instant.now().toEpochMilli();

      if (profile.getBanUntil() > 0 && profile.getBanUntil() <= now) {
        profile.unban();
        profile.save();
        return;
      }

      if (profile.isBanned()) {
        String reason = profile.getBanReason() == null ? "" : profile.getBanReason();
        String bannedBy = profile.getBannedBy() == null ? "" : profile.getBannedBy();

        String kickMsg;
        if (profile.getBanUntil() == 0) {
          kickMsg = String.valueOf(this.messageConfig.banKick
                  .replace("{reason}", reason)
                  .replace("{bannedBy}", bannedBy))
              .replace("&", "§");
        } else {
          long banUntil = profile.getBanUntil();
          kickMsg = String.valueOf(this.messageConfig.tempBanKick
                  .replace("{reason}", reason)
                  .replace("{bannedBy}", bannedBy)
                  .replace("{banExpire}",
                      DateUtil.format(Instant.ofEpochMilli(banUntil))))
              .replace("&", "§");
        }

        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, kickMsg);
        return;
      }

      if (profile.hasIpBan(ip)) {
        String kickMsg = this.messageConfig.ipBanKick
            .replace("{reason}", profile.getBanReason() == null ? "" : profile.getBanReason())
            .replace("{bannedBy}", profile.getBannedBy() == null ? "" : profile.getBannedBy())
            .replace("&", "§");

        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, kickMsg);
      }

    } catch (CompletionException | NullPointerException exception) {
      System.err.println("[Dream-Bans] Błąd podczas ładowania profilu " + name + " (" + uuid + ")");
      exception.printStackTrace();

      String errorKickMsg = "§cWystąpił krytyczny błąd podczas ładowania Twojego profilu. Spróbuj ponownie. Błąd: " + exception.getMessage();
      event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, errorKickMsg);
    }
  }
}