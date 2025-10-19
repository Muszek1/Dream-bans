package cc.dreamcode.bans.punishments;

import eu.okaeri.configs.OkaeriConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class Warn extends OkaeriConfig {

  private final String warnedBy;
  private String reason;
  private long date;

}