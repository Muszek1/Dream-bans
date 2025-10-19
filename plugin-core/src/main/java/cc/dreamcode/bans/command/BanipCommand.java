package cc.dreamcode.bans.command;

import cc.dreamcode.bans.profile.ProfileService;
import cc.dreamcode.bans.service.BanService;
import cc.dreamcode.command.CommandBase;
import cc.dreamcode.command.annotation.Arg;
import cc.dreamcode.command.annotation.Command;
import cc.dreamcode.command.annotation.Executor;
import cc.dreamcode.command.annotation.OptArg;
import cc.dreamcode.command.annotation.Permission;
import eu.okaeri.injector.annotation.Inject;
import eu.okaeri.tasker.bukkit.BukkitTasker;
import lombok.RequiredArgsConstructor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

@Command(name = "banip")
@Permission("dream-bans.banip")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class BanipCommand implements CommandBase {

  private final BanService banService;
  private final ProfileService profileService;
  private final cc.dreamcode.bans.config.MessageConfig messageConfig;
  private final BukkitTasker tasker;

  @Executor(description = "Banuje gracza po IP.")
  public void banIpPlayer(CommandSender sender, @Arg("target") OfflinePlayer target,
      @OptArg("reason") String reason) {

    String finalReason = (reason == null || reason.isEmpty())
        ? this.messageConfig.defaultReason
        : reason;

    this.profileService.loadAsync(target.getUniqueId(), target.getName())
        .whenComplete((profile, exception) -> {

          if (exception != null) {
            this.tasker.newChain().runSync(() -> {
              this.messageConfig.errorWhenLoadingProfile.with("player", target.getName()).send(sender);
            }).execute();
            return;
          }

          this.tasker.newChain().runSync(() -> {

            String ip = profile.getLastIp();

            if (ip == null || ip.isEmpty()) {
              this.messageConfig.noIpFound.with("player", target.getName()).send(sender);
              return;
            }

            this.banService.createIpBan(sender, target.getUniqueId(), target.getName(), ip, finalReason);

          }).execute();
        });
  }
}