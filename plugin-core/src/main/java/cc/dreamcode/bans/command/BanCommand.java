package cc.dreamcode.bans.command;

import cc.dreamcode.bans.config.MessageConfig;
import cc.dreamcode.bans.service.BanService;
import cc.dreamcode.command.CommandBase;
import cc.dreamcode.command.annotation.Arg;
import cc.dreamcode.command.annotation.Command;
import cc.dreamcode.command.annotation.Completion;
import cc.dreamcode.command.annotation.Executor;
import cc.dreamcode.command.annotation.OptArg;
import cc.dreamcode.command.annotation.Permission;
import eu.okaeri.injector.annotation.Inject;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;


@Command(name = "ban")
@Permission("dream-bans.ban")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class BanCommand implements CommandBase {

  private final BanService banService;
  private final MessageConfig messageConfig;

  @Completion(arg = "target", value = "@allplayers")
  @Executor(description = "Banuje gracza.")
  public void banPlayer(CommandSender sender, @Arg("target") OfflinePlayer target,
      @OptArg("reason") String reason) {

    if (target == null) {
      sender.sendMessage("§cNie znaleziono gracza.");
      return;
    } else {
      target.getUniqueId();
    }

    if (reason == null || reason.trim().isEmpty()) {
      reason = this.messageConfig.defaultReason;
    }

    this.banService.createBan(sender, target.getUniqueId(),
        Objects.requireNonNull(target.getName()), reason);
  }
}
