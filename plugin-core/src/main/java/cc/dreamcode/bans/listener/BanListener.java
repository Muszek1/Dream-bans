// java
package cc.dreamcode.bans.listener;

import cc.dreamcode.bans.config.MessageConfig;
import cc.dreamcode.bans.config.PluginConfig;
import cc.dreamcode.bans.profile.ProfileService;
import eu.okaeri.injector.annotation.Inject;
import eu.okaeri.tasker.bukkit.BukkitTasker;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class BanListener implements Listener {

  private final ProfileService profileService;
  private final MessageConfig messageConfig;
  private final PluginConfig pluginConfig;
  private final BukkitTasker tasker;

  @EventHandler
  public void onPreLogin(AsyncPlayerPreLoginEvent event) {
    final String name = event.getName();
    final UUID uuid = event.getUniqueId();
    final String ip = event.getAddress().getHostAddress();

    if (this.pluginConfig.blacklistPlayers.stream()
        .anyMatch(player -> player.equalsIgnoreCase(name))) {

      String kickMsg = this.messageConfig.blacklistKick
          .replace("{reason}", "Blacklista z configu")
          .replace("{blacklistedBy}", "Console")
          .replace("&", "§");

      event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, kickMsg);
      return;
    }

    this.profileService.loadAsync(uuid, name)
        .whenComplete((profile, exception) -> {

          if (exception != null) {
            System.err.println("[Dream-Bans] Błąd podczas ładowania profilu " + name + " (" + uuid + ")");
            exception.printStackTrace();

            String errorKickMsg = "§cWystąpił krytyczny błąd podczas ładowania Twojego profilu. Spróbuj ponownie. Błąd: " + exception.getMessage();
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, errorKickMsg);
            return;
          }

          this.tasker.newChain()
              .run(() -> {
                profile.setLastIp(ip);
                profile.save();
              })
              .run(() -> {
                long now = Instant.now().toEpochMilli();

                if (profile.getBanUntil() > 0 && profile.getBanUntil() <= now) {
                  profile.setBanned(false);
                  profile.setBanReason(null);
                  profile.setBannedBy(null);
                  profile.setBanUntil(0);
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
                                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(banUntil))))
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
              })
              .execute();
        });
  }
}