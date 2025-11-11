package cc.dreamcode.bans.command;

import cc.dreamcode.bans.config.MessageConfig;
import cc.dreamcode.command.CommandBase;
import cc.dreamcode.command.annotation.Arg;
import cc.dreamcode.command.annotation.Args;
import cc.dreamcode.command.annotation.Command;
import cc.dreamcode.command.annotation.Completion;
import cc.dreamcode.command.annotation.Executor;
import cc.dreamcode.command.annotation.Permission;
import eu.okaeri.injector.annotation.Inject;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command(name = "warn")
@Permission("dream-bans.warn")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class WarnCommand implements CommandBase {

  private final MessageConfig messageConfig;

  @Completion(arg = "target", value = "@allplayers")
  @Executor(description = "Nadaje ostrzeżenie graczowi.")
  public void warnPlayer(CommandSender sender,
      @Arg("target") Player target,
      @Args(min = 1) String[] args) {

    String reason = String.join(" ", args).trim();
    if (reason.isEmpty()) {
      reason = this.messageConfig.defaultReason;
    }

    this.messageConfig.actionBarWarn
        .with("player", target.getName())
        .with("issuer", sender.getName())
        .with("reason", reason)
        .with("&", "§")
        .send(target);

    this.messageConfig.warnNotify
        .with("player", target.getName())
        .with("issuer", sender.getName())
        .with("reason", reason)
        .send(sender);
  }
}
