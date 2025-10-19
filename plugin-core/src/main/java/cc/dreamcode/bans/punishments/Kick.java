package cc.dreamcode.bans.punishments;

import eu.okaeri.configs.OkaeriConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class Kick extends OkaeriConfig {

  private final String kickedBy;
  private String reason;
  private long date;

}
