package cc.dreamcode.bans.profile;

import eu.okaeri.injector.annotation.Inject;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ProfileController implements Listener {

  private final ProfileService profileService;

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    profileService.handleJoin(event.getPlayer());
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    profileService.handleQuit(event.getPlayer());
  }
}
