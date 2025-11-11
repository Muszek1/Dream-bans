// java
package cc.dreamcode.bans.listener;

import cc.dreamcode.bans.config.MessageConfig;
import cc.dreamcode.bans.config.PluginConfig;
import cc.dreamcode.bans.profile.Profile;
import cc.dreamcode.bans.profile.ProfileService;
import eu.okaeri.injector.annotation.Inject;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class MuteCommandListener implements Listener {

  private final ProfileService profileService;
  private final PluginConfig pluginConfig;
  private final MessageConfig messageConfig;

  @EventHandler(priority = EventPriority.LOW)
  public void onCommand(PlayerCommandPreprocessEvent event) {
    if (event.isCancelled()) {
      return;
    }

    Player player = event.getPlayer();

    Profile profile = this.profileService.getProfileCache().get(player.getUniqueId());

    if (profile == null) {
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
  }
}