package cc.dreamcode.bans.command;

import cc.dreamcode.bans.service.MuteService;
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

@Command(name = "unmute")
@Permission("dream-bans.unmute")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class UnmuteCommand implements CommandBase {

  private final MuteService muteService;
  private final cc.dreamcode.bans.config.MessageConfig messageConfig;

  @Completion(arg = "target", value = "@allplayers")
  @Executor(description = "Odbanowuje mute gracza.")
  public BukkitNotice unmutePlayer(CommandSender sender,
      @Arg("target") org.bukkit.OfflinePlayer target) {

    this.muteService.removeMute(sender, target.getUniqueId(), target.getName());

    return this.messageConfig.unmuteSuccess.with("player", target.getName());
  }
}
