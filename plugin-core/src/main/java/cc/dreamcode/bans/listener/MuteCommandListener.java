// java
package cc.dreamcode.bans.listener;

import cc.dreamcode.bans.config.MessageConfig;
import cc.dreamcode.bans.config.PluginConfig;
import cc.dreamcode.bans.profile.ProfileService;
import eu.okaeri.injector.annotation.Inject;
import eu.okaeri.tasker.bukkit.BukkitTasker;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class MuteCommandListener implements Listener {

  private final ProfileService profileService;
  private final PluginConfig pluginConfig;
  private final MessageConfig messageConfig;
  private final BukkitTasker tasker;

  @EventHandler
  public void onCommand(PlayerCommandPreprocessEvent event) {

    Player player = event.getPlayer();

    this.profileService.loadAsync(player.getUniqueId(), player.getName())
        .whenComplete((profile, exception) -> {

          if (exception != null) {
            System.err.println("[Dream-Bans] Błąd podczas ładowania profilu " + player.getName() + " w MuteCommandListener.");
            exception.printStackTrace();
            return;
          }

          this.tasker.newChain().runSync(() -> {

            if (event.isCancelled()) {
              return;
            }

            if (!profile.isMuted()) {
              return;
            }

            String cmd = event.getMessage().split(" ")[0].toLowerCase().substring(1);

            boolean isCommandBlocked = this.pluginConfig.muteBlockedCommands.stream()
                .anyMatch(blockedCmd -> blockedCmd.equalsIgnoreCase(cmd));

            if (isCommandBlocked) {
              event.setCancelled(true);
              this.messageConfig.muteCommandBlocked.send(player);
            }
          }).execute();
        });
  }
}