package cc.dreamcode.bans.command;

import cc.dreamcode.bans.profile.ProfileService;
import cc.dreamcode.bans.service.BanService;
import cc.dreamcode.command.CommandBase;
import cc.dreamcode.command.annotation.Arg;
import cc.dreamcode.command.annotation.Command;
import cc.dreamcode.command.annotation.Executor;
import cc.dreamcode.command.annotation.Permission;
import eu.okaeri.injector.annotation.Inject;
import eu.okaeri.tasker.bukkit.BukkitTasker;
import lombok.RequiredArgsConstructor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

@Command(name = "unbanip")
@Permission("dream-bans.unbanip")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class UnbanipCommand implements CommandBase {

  private final BanService banService;
  private final ProfileService profileService;
  private final cc.dreamcode.bans.config.MessageConfig messageConfig;
  private final BukkitTasker tasker;

  @Executor(description = "Odbanowuje gracza po IP.")
  public void unbanIpPlayer(CommandSender sender, @Arg("target") OfflinePlayer target) {

    this.profileService.loadAsync(target.getUniqueId(), target.getName())
        .whenComplete((profile, exception) -> {

          if (exception != null) {
            this.tasker.newChain().runSync(() -> {
              this.messageConfig.errorWhenLoadingProfile.with("player", target.getName()).send(sender);
            }).execute();
            return;
          }

          this.tasker.newChain().run(() -> {

            String ip = profile.getLastIp();

            if (ip == null || ip.isEmpty()) {
              this.tasker.newChain().runSync(() -> {
                this.messageConfig.noIpFound.with("player", target.getName()).send(sender);
              }).execute();
              return;
            }

            this.banService.removeIpBan(sender, ip);

            this.tasker.newChain().runSync(() -> {
              this.messageConfig.ipUnbanSuccess.with("player", target.getName()).with("ip", ip).send(sender);
            }).execute();

          }).execute();
        });
  }
}