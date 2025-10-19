// java
package cc.dreamcode.bans.listener;

import cc.dreamcode.bans.config.MessageConfig;
import cc.dreamcode.bans.profile.ProfileService;
import eu.okaeri.injector.annotation.Inject;
import eu.okaeri.tasker.bukkit.BukkitTasker;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class MuteListener implements Listener {

  private final ProfileService profileService;
  private final MessageConfig messageConfig;
  private final BukkitTasker tasker;

  @EventHandler
  public void onChat(AsyncPlayerChatEvent event) {
    Player player = event.getPlayer();

    this.profileService.loadAsync(player.getUniqueId(), player.getName())
        .whenComplete((profile, exception) -> {

          if (exception != null) {
            System.err.println("[Dream-Bans] Błąd podczas ładowania profilu " + player.getName()
                + " w MuteListener.");
            exception.printStackTrace();
            return;
          }

          this.tasker.newChain().run(() -> {
            long now = Instant.now().toEpochMilli();

            if (profile.getMuteUntil() > 0 && profile.getMuteUntil() <= now) {
              profile.unmute();
              profile.save();
              return;
            }
          }).runSync(() -> {

            if (event.isCancelled()) {
              return;
            }

            if (profile.isMuted()) {
              event.setCancelled(true);

              if (profile.getMuteUntil() > 0) {
                this.messageConfig.tempMuteChat.with("reason",
                        profile.getMuteReason() == null ? "" : profile.getMuteReason())
                    .with("mutedBy", profile.getMutedBy() == null ? "" : profile.getMutedBy())
                    .with("muteExpire", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(
                        new Date(profile.getMuteUntil()))).send(player);

              } else {
                this.messageConfig.muteChat.with("reason",
                        profile.getMuteReason() == null ? "" : profile.getMuteReason())
                    .with("mutedBy", profile.getMutedBy() == null ? "" : profile.getMutedBy())
                    .send(player);
              }
            }
          }).execute();
        });
  }
}