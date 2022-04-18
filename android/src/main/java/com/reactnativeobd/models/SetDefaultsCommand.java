package com.reactnativeobd.models;

import com.github.pires.obd.commands.protocol.ObdProtocolCommand;

public class SetDefaultsCommand extends ObdProtocolCommand {
  /**
   * <p>Constructor for ObdResetCommand.</p>
   */
  public SetDefaultsCommand() {
    super("AT D");
  }

  /**
   * <p>Constructor for ObdResetCommand.</p>
   *

   */
  public SetDefaultsCommand(SetDefaultsCommand other) {
    super(other);
  }

  /** {@inheritDoc} */
  @Override
  public String getFormattedResult() {
    return getResult();
  }

  /** {@inheritDoc} */
  @Override
  public String getName() {
    return "Set Defaults OBD";
  }
}
