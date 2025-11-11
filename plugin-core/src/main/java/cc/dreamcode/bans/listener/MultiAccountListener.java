// java
package cc.dreamcode.bans.listener;

import cc.dreamcode.bans.config.MessageConfig;
import cc.dreamcode.bans.profile.Profile;
import cc.dreamcode.bans.profile.ProfileRepository;
import cc.dreamcode.bans.profile.ProfileService;
import eu.okaeri.injector.annotation.Inject;
import eu.okaeri.tasker.bukkit.BukkitTasker;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class MultiAccountListener implements Listener {

  private final ProfileRepository profileRepository;
  private final ProfileService profileService;
  private final MessageConfig messageConfig;
  private final BukkitTasker tasker;

  @EventHandler
  public void onPreLogin(AsyncPlayerPreLoginEvent event) {
    final String ip = event.getAddress().getHostAddress();
    final String joiningName = event.getName();

    this.profileService.loadAsync(event.getUniqueId(), joiningName)
        .whenComplete((joiningProfile, exception) -> {

          if (exception != null) {
            System.err.println("[Dream-Bans] Błąd podczas ładowania profilu " + joiningName + " (" + event.getUniqueId() + ")");
            exception.printStackTrace();
            return;
          }

          this.tasker.newChain()
              .run(() -> {
                joiningProfile.setLastIp(ip);
                joiningProfile.save();
              })
              .supply(() ->
                  this.profileRepository.findAll().stream()
                      .filter(profile -> profile.getLastIp() != null)
                      .filter(profile -> profile.getLastIp().equals(ip))
                      .filter(profile -> !profile.getUniqueId().equals(event.getUniqueId()))
                      .toList()
              )
              .acceptSync(sameIpProfiles -> {

                if (sameIpProfiles.isEmpty()) {
                  return;
                }

                for (Player admin : Bukkit.getOnlinePlayers()) {
                  if (admin.hasPermission("dream-bans.alert.multikonto")) {
                    for (Profile profile : sameIpProfiles) {
                      String profileName = profile.getName();

                      if (profile.isBanned()) {
                        this.messageConfig.multiAccountAlert
                            .with("joining", joiningName)
                            .with("banned", profileName)
                            .send(admin);
                      } else {
                        this.messageConfig.multiAccountAlert
                            .with("joining", joiningName)
                            .with("banned", profileName + " (brak bana)")
                            .send(admin);
                      }
                    }
                  }
                }
              })
              .execute();
        });
  }
}