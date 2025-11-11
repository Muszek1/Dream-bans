package cc.dreamcode.bans.listener;

import cc.dreamcode.bans.config.MessageConfig;
import cc.dreamcode.bans.profile.Profile;
import cc.dreamcode.bans.profile.ProfileService;
import cc.dreamcode.utilities.DateUtil;
import eu.okaeri.injector.annotation.Inject;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class MuteListener implements Listener {

  private final ProfileService profileService;
  private final MessageConfig messageConfig;

  @EventHandler(priority = EventPriority.LOW)
  public void onChat(AsyncPlayerChatEvent event) {
    if (event.isCancelled()) {
      return;
    }

    Player player = event.getPlayer();

    Profile profile = this.profileService.getProfileCache().get(player.getUniqueId());

    if (profile == null) {
      return;
    }

    long now = Instant.now().toEpochMilli();
    if (profile.getMuteUntil() > 0 && profile.getMuteUntil() <= now) {
      profile.unmute();
      this.profileService.saveAsync(profile);
      return;
    }

    if (profile.isMuted()) {
      event.setCancelled(true);

      if (profile.getMuteUntil() > 0) {
        this.messageConfig.tempMuteChat.with("reason",
                profile.getMuteReason() == null ? "" : profile.getMuteReason())
            .with("mutedBy", profile.getMutedBy() == null ? "" : profile.getMutedBy())
            .with("muteExpire", DateUtil.format(Instant.ofEpochMilli(profile.getMuteUntil()))).send(player);

      } else {
        this.messageConfig.muteChat.with("reason",
                profile.getMuteReason() == null ? "" : profile.getMuteReason())
            .with("mutedBy", profile.getMutedBy() == null ? "" : profile.getMutedBy())
            .send(player);
      }
    }
  }
}