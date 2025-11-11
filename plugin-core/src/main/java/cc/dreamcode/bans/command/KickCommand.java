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

@Command(name = "kick")
@Permission("dream-bans.kick")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class KickCommand implements CommandBase {

  private final MessageConfig messageConfig;

  @Completion(arg = "target", value = "@allplayers")
  @Executor(description = "Wyrzuca gracza z serwera.")
  public void kickPlayer(CommandSender sender,
      @Arg("target") Player target,
      @Args(min = 1) String[] args) {

    String reason = String.join(" ", args).trim();
    if (reason.isEmpty()) {
      reason = this.messageConfig.defaultReason;
    }

    String kickMsg = this.messageConfig.kickFormat
        .replace("{reason}", reason)
        .replace("{issuer}", sender.getName())
        .replace("&", "§");
    target.kickPlayer(kickMsg);

    this.messageConfig.kickBroadcast
        .with("player", target.getName())
        .with("issuer", sender.getName())
        .with("reason", reason)
        .sendAll();

    this.messageConfig.kickSuccess
        .with("player", target.getName())
        .with("issuer", sender.getName())
        .with("reason", reason)
        .send(sender);
  }
}
