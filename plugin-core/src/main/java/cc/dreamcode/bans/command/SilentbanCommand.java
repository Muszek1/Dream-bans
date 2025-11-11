package cc.dreamcode.bans.command;

import cc.dreamcode.bans.config.MessageConfig;
import cc.dreamcode.bans.service.BanService;
import cc.dreamcode.command.CommandBase;
import cc.dreamcode.command.annotation.Arg;
import cc.dreamcode.command.annotation.Args;
import cc.dreamcode.command.annotation.Command;
import cc.dreamcode.command.annotation.Completion;
import cc.dreamcode.command.annotation.Executor;
import cc.dreamcode.command.annotation.Permission;
import eu.okaeri.injector.annotation.Inject;
import lombok.RequiredArgsConstructor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

@Command(name = "silentban")
@Permission("dream-bans.silentban")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class SilentbanCommand implements CommandBase {

  private final BanService banService;
  private final MessageConfig messageConfig;

  @Completion(arg = "target", value = "@allplayers")
  @Executor(description = "Banuje gracza bez publicznego ogłoszenia.")
  public void silentBanPlayer(CommandSender sender,
      @Arg("target") OfflinePlayer target,
      @Args(min = 1) String[] args) {

    String reason = String.join(" ", args).trim();
    if (reason.isEmpty()) {
      reason = this.messageConfig.defaultReason;
    }

    this.banService.createBan(sender, target.getUniqueId(), target.getName(), reason);

    final String finalReason = reason;
    this.messageConfig.silentBanNotify.with("player", target.getName())
        .with("issuer", sender.getName()).with("reason", finalReason)
        .sendPermitted("dream-chat.silentinfo");

    this.messageConfig.silentBanSuccess.with("player", target.getName())
        .with("issuer", sender.getName()).with("reason", reason).send(sender);
  }
}
