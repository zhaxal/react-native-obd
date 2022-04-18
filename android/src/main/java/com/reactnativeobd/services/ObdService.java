package com.reactnativeobd.services;

import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.HeadersOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.ObdResetCommand;
import com.github.pires.obd.enums.AvailableCommandNames;
import com.reactnativeobd.models.SetDefaultsCommand;


import java.util.ArrayList;

public class ObdService {

  public static String LookUpCommand(String txt) {
    for (AvailableCommandNames item : AvailableCommandNames.values()) {
      if (item.getValue().equals(txt)) return item.name();
    }
    return txt;
  }

  public static ArrayList<ObdCommand> getInitCommands() {
    ArrayList<ObdCommand> cmds = new ArrayList<>();
    cmds.add(new SetDefaultsCommand());
    cmds.add(new ObdResetCommand());
    cmds.add(new EchoOffCommand());
    cmds.add(new LineFeedOffCommand());
    cmds.add(new SetDefaultsCommand());
    cmds.add(new HeadersOffCommand());

    return cmds;
  }

  public static ArrayList<ObdCommand> getCommands() {
    ArrayList<ObdCommand> cmds = new ArrayList<>();

    // Control
//    cmds.add(new ModuleVoltageCommand());
//    cmds.add(new EquivalentRatioCommand());
//    cmds.add(new DistanceMILOnCommand());
//    cmds.add(new DtcNumberCommand());
//    cmds.add(new TimingAdvanceCommand());
//    cmds.add(new TroubleCodesCommand());
//    cmds.add(new VinCommand());

    // Engine
//    cmds.add(new LoadCommand());
    cmds.add(new RPMCommand());
//    cmds.add(new RuntimeCommand());
//    cmds.add(new MassAirFlowCommand());
//    cmds.add(new ThrottlePositionCommand());
//
//    // Fuel
//    cmds.add(new FindFuelTypeCommand());
//    cmds.add(new ConsumptionRateCommand());
//    // cmds.add(new AverageFuelEconomyObdCommand());
//    //cmds.add(new FuelEconomyCommand());
//    cmds.add(new FuelLevelCommand());
//    // cmds.add(new FuelEconomyMAPObdCommand());
//    // cmds.add(new FuelEconomyCommandedMAPObdCommand());
//    cmds.add(new FuelTrimCommand(FuelTrim.LONG_TERM_BANK_1));
//    cmds.add(new FuelTrimCommand(FuelTrim.LONG_TERM_BANK_2));
//    cmds.add(new FuelTrimCommand(FuelTrim.SHORT_TERM_BANK_1));
//    cmds.add(new FuelTrimCommand(FuelTrim.SHORT_TERM_BANK_2));
//    cmds.add(new AirFuelRatioCommand());
//    cmds.add(new WidebandAirFuelRatioCommand());
//    cmds.add(new OilTempCommand());
//
//    // Pressure
//    cmds.add(new BarometricPressureCommand());
//    cmds.add(new FuelPressureCommand());
//    cmds.add(new FuelRailPressureCommand());
//    cmds.add(new IntakeManifoldPressureCommand());
//
//    // Temperature
//    cmds.add(new AirIntakeTemperatureCommand());
//    cmds.add(new AmbientAirTemperatureCommand());
//    cmds.add(new EngineCoolantTemperatureCommand());
//
//    // Misc
//    cmds.add(new SpeedCommand());


    return cmds;
  }
}
