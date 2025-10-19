package cc.dreamcode.bans.command;

import cc.dreamcode.bans.service.BanService;
import cc.dreamcode.command.CommandBase;
import cc.dreamcode.command.annotation.Arg;
import cc.dreamcode.command.annotation.Command;
import cc.dreamcode.command.annotation.Completion;
import cc.dreamcode.command.annotation.Executor;
import cc.dreamcode.command.annotation.Permission;
import cc.dreamcode.notice.bukkit.BukkitNotice;
import eu.okaeri.injector.annotation.Inject;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;

@Command(name = "unban")
@Permission("dream-bans.unban")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class UnbanCommand implements CommandBase {

  private final BanService banService;
  private final cc.dreamcode.bans.config.MessageConfig messageConfig;

  @Completion(arg = "target", value = "@allplayers")
  @Executor(description = "Odbanowuje gracza.")
  public BukkitNotice unbanPlayer(CommandSender sender,
      @Arg("target") org.bukkit.OfflinePlayer target) {

    this.banService.removeBan(sender, target.getUniqueId(), target.getName());

    return this.messageConfig.unbanSuccess.with("player", target.getName());
  }
}