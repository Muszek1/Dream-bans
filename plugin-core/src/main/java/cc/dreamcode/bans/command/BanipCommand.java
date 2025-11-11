package cc.dreamcode.bans.command;

import cc.dreamcode.bans.config.MessageConfig;
import cc.dreamcode.bans.profile.ProfileService;
import cc.dreamcode.bans.service.BanService;
import cc.dreamcode.command.CommandBase;
import cc.dreamcode.command.annotation.Arg;
import cc.dreamcode.command.annotation.Args;
import cc.dreamcode.command.annotation.Command;
import cc.dreamcode.command.annotation.Executor;
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
  private final MessageConfig messageConfig;
  private final BukkitTasker tasker;

  @Executor(description = "Banuje gracza po IP.")
  public void banIpPlayer(CommandSender sender,
      @Arg("target") OfflinePlayer target,
      @Args(min = 1) String[] args) {

    String reason = String.join(" ", args).trim();
    if (reason.isEmpty()) {
      reason = this.messageConfig.defaultReason;
    }

    String finalReason = reason;
    this.profileService.loadAsync(target.getUniqueId(), target.getName()).thenAccept(profile -> {
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
