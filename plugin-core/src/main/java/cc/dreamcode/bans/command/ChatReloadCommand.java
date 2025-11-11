package cc.dreamcode.bans.command;

import cc.dreamcode.bans.config.MessageConfig;
import cc.dreamcode.bans.config.PluginConfig;
import cc.dreamcode.command.CommandBase;
import cc.dreamcode.command.annotation.Async;
import cc.dreamcode.command.annotation.Command;
import cc.dreamcode.command.annotation.Executor;
import cc.dreamcode.command.annotation.Permission;
import cc.dreamcode.notice.bukkit.BukkitNotice;
import cc.dreamcode.utilities.TimeUtil;
import eu.okaeri.configs.exception.OkaeriException;
import eu.okaeri.injector.annotation.Inject;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender; // Potrzebny import

@Command(name = "dreambans")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ChatReloadCommand implements CommandBase {

  private final PluginConfig pluginConfig;
  private final MessageConfig messageConfig;

  @Async
  @Permission("dream-bans.reload")
  @Executor(path = "reload", description = "Przeładowuje konfiguracje Dream-Bans.")
  BukkitNotice reload(CommandSender sender) {
    final long start = System.currentTimeMillis();
    try {
      this.messageConfig.load();
      this.pluginConfig.load();

      return this.messageConfig.reloaded.with("time",
          TimeUtil.format(System.currentTimeMillis() - start));
    } catch (NullPointerException | OkaeriException e) {
      e.printStackTrace();
      return this.messageConfig.reloadError.with("error", e.getMessage());
    }
  }
}