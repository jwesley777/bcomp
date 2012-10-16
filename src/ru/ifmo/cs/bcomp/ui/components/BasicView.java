/*
 * $Id$
 */

package ru.ifmo.cs.bcomp.ui.components;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.EnumMap;
import ru.ifmo.cs.bcomp.CPU;
import ru.ifmo.cs.bcomp.ControlSignal;
import ru.ifmo.cs.bcomp.ui.GUI;
import static ru.ifmo.cs.bcomp.ui.components.DisplayStyles.*;

/**
 *
 * @author Dmitry Afanasiev <KOT@MATPOCKuH.Ru>
 */
public class BasicView extends BCompPanel {
	private CPU cpu;
	private ComponentManager cmanager;
	private ArrayList<BusView> openbuses = new ArrayList<BusView>();
	private static final ControlSignal[] bustypes = {
		ControlSignal.DATA_TO_ALU,
		ControlSignal.INSTR_TO_ALU,
		ControlSignal.IP_TO_ALU,
		ControlSignal.ACCUM_TO_ALU,
		ControlSignal.KEY_TO_ALU,
		ControlSignal.BUF_TO_ADDR,
		ControlSignal.BUF_TO_DATA,
		ControlSignal.BUF_TO_INSTR,
		ControlSignal.BUF_TO_IP,
		ControlSignal.BUF_TO_ACCUM,
		ControlSignal.MEMORY_READ,
		ControlSignal.MEMORY_WRITE
	};
	private EnumMap<ControlSignal, BusView[]> buses =
		new EnumMap<ControlSignal, BusView[]>(ControlSignal.class);
	private BusView busInstr2CU = new BusView(openbuses, new int[][] {
		{BUS_INSTR_TO_CU_X, BUS_FROM_INSTR_Y},
		{BUS_INSTR_TO_CU_X, BUS_INSTR_TO_CU_Y}
	});
	private SignalListener[] listeners;
	private RunningCycleView cycleview;

	public BasicView(GUI gui) {
		cpu = gui.getCPU();
		cmanager = gui.getComponentManager();

		BusView addrbus = new BusView(openbuses, new int[][] {
			{BUS_ADDR_X1, BUS_TO_ADDR_Y},
			{BUS_ADDR_X2, BUS_TO_ADDR_Y}
		});

		buses.put(ControlSignal.DATA_TO_ALU, new BusView[] {new BusView(openbuses, new int[][] {
			{BUS_RIGHT_X1, BUS_FROM_DATA_Y},
			{BUS_RIGHT_X1, BUS_LEFT_INPUT_UP},
			{BUS_RIGHT_X, BUS_LEFT_INPUT_UP},
			{BUS_RIGHT_X, BUS_LEFT_INPUT_DOWN}
		})});
		buses.put(ControlSignal.INSTR_TO_ALU, new BusView[] {new BusView(openbuses, new int[][] {
			{BUS_FROM_INSTR_X, BUS_FROM_INSTR_Y},
			{BUS_FROM_INSTR_X, BUS_LEFT_INPUT_UP},
			{BUS_RIGHT_X, BUS_LEFT_INPUT_UP},
			{BUS_RIGHT_X, BUS_LEFT_INPUT_DOWN}
		})});
		buses.put(ControlSignal.IP_TO_ALU, new BusView[] {new BusView(openbuses, new int[][] {
			{BUS_FROM_IP_X, BUS_FROM_IP_Y},
			{BUS_RIGHT_X1, BUS_FROM_IP_Y},
			{BUS_RIGHT_X1, BUS_LEFT_INPUT_UP},
			{BUS_RIGHT_X, BUS_LEFT_INPUT_UP},
			{BUS_RIGHT_X, BUS_LEFT_INPUT_DOWN}
		})});
		buses.put(ControlSignal.ACCUM_TO_ALU, new BusView[] {new BusView(openbuses, new int[][] {
			{BUS_FROM_ACCUM_X, BUS_FROM_ACCUM_Y},
			{BUS_LEFT_INPUT_X1, BUS_FROM_ACCUM_Y},
			{BUS_LEFT_INPUT_X1, BUS_LEFT_INPUT_UP},
			{BUS_LEFT_INPUT_X, BUS_LEFT_INPUT_UP},
			{BUS_LEFT_INPUT_X, BUS_LEFT_INPUT_DOWN}
		})});
		buses.put(ControlSignal.KEY_TO_ALU, new BusView[] {new BusView(openbuses, new int[][] {
			{BUS_LEFT_INPUT_X1, BUS_KEY_ALU},
			{BUS_LEFT_INPUT_X1, BUS_LEFT_INPUT_UP},
			{BUS_LEFT_INPUT_X, BUS_LEFT_INPUT_UP},
			{BUS_LEFT_INPUT_X, BUS_LEFT_INPUT_DOWN}
		})});
		buses.put(ControlSignal.BUF_TO_ADDR, new BusView[] {new BusView(openbuses, new int[][] {
			{FROM_ALU_X, FROM_ALU_Y},
			{FROM_ALU_X, FROM_ALU_Y1},
			{BUS_RIGHT_TO_X, FROM_ALU_Y1},
			{BUS_RIGHT_TO_X, BUS_TO_ADDR_Y},
			{BUS_TO_ADDR_X, BUS_TO_ADDR_Y}
		})});
		buses.put(ControlSignal.BUF_TO_DATA, new BusView[] {new BusView(openbuses, new int[][] {
			{FROM_ALU_X, FROM_ALU_Y},
			{FROM_ALU_X, FROM_ALU_Y1},
			{BUS_RIGHT_TO_X, FROM_ALU_Y1},
			{BUS_RIGHT_TO_X, BUS_TO_DATA_Y},
			{BUS_TO_DATA_X, BUS_TO_DATA_Y}
		})});
		buses.put(ControlSignal.BUF_TO_INSTR, new BusView[] {new BusView(openbuses, new int[][] {
			{FROM_ALU_X, FROM_ALU_Y},
			{FROM_ALU_X, FROM_ALU_Y1},
			{BUS_RIGHT_TO_X, FROM_ALU_Y1},
			{BUS_RIGHT_TO_X, BUS_TO_ADDR_Y},
			{BUS_TO_INSTR_X, BUS_TO_ADDR_Y}
		})});
		buses.put(ControlSignal.BUF_TO_IP, new BusView[] {new BusView(openbuses, new int[][] {
			{FROM_ALU_X, FROM_ALU_Y},
			{FROM_ALU_X, FROM_ALU_Y1},
			{BUS_RIGHT_TO_X, FROM_ALU_Y1},
			{BUS_RIGHT_TO_X, BUS_FROM_IP_Y},
			{BUS_TO_DATA_X, BUS_FROM_IP_Y}
		})});
		buses.put(ControlSignal.BUF_TO_ACCUM, new BusView[] {new BusView(openbuses, new int[][] {
			{FROM_ALU_X, FROM_ALU_Y},
			{FROM_ALU_X, TO_ACCUM_Y}
		})});
		buses.put(ControlSignal.MEMORY_READ, new BusView[] { addrbus, new BusView(openbuses, new int[][] {
			{BUS_READ_X2, BUS_READ_Y},
			{BUS_READ_X1, BUS_READ_Y}
		})});
		buses.put(ControlSignal.MEMORY_WRITE, new BusView[] { addrbus, new BusView(openbuses, new int[][] {
			{BUS_ADDR_X1, BUS_WRITE_Y},
			{BUS_ADDR_X2, BUS_WRITE_Y}
		})});

		add(new ALUView(REG_C_X_BV, ALU_Y, ALU_WIDTH, ALU_HEIGHT));

		ArrayList<SignalListener> lsnr = new ArrayList<SignalListener>();
		for (int i = 0; i < bustypes.length; i++)
			for (BusView bus : buses.get(bustypes[i]))
				lsnr.add(cmanager.createSignalListener(bus, bustypes[i]));

		lsnr.add(cmanager.createSignalListener(CPU.Reg.STATE,
			ControlSignal.BUF_TO_STATE_C, ControlSignal.CLEAR_STATE_C, ControlSignal.SET_STATE_C));

		listeners = new SignalListener[lsnr.size()];

		for (int i = 0; i < lsnr.size(); i++)
			listeners[i] = lsnr.get(i);

		cycleview = new RunningCycleView(cpu, REG_INSTR_X_BV, CYCLEVIEW_Y);
		add(cycleview);
	}

	@Override
	public void paintComponent(Graphics g) {
		for (ControlSignal bustype : bustypes)
			for (BusView bus : buses.get(bustype))
				bus.draw(g, Color.GRAY);

		busInstr2CU.draw(g, Color.GRAY);
	}

	@Override
	public void panelActivate() {
		RegisterView reg = cmanager.getRegisterView(CPU.Reg.ADDR);
		reg.setProperties("Регистр адреса", REG_ACCUM_X_BV, REG_ADDR_Y_BV, false);
		add(reg);

		reg = cmanager.getRegisterView(CPU.Reg.DATA);
		reg.setProperties("Регистр данных", REG_ACCUM_X_BV, REG_DATA_Y_BV, false);
		add(reg);

		reg = cmanager.getRegisterView(CPU.Reg.IP);
		reg.setProperties("Счётчик команд", REG_IP_X_BV, REG_IP_Y_BV, false);
		add(reg);

		reg = cmanager.getRegisterView(CPU.Reg.INSTR);
		reg.setProperties("Регистр команд", REG_INSTR_X_BV, REG_ADDR_Y_BV, false);
		add(reg);

		reg = cmanager.getRegisterView(CPU.Reg.ACCUM);
		reg.setProperties("Аккумулятор", REG_ACCUM_X_BV, REG_ACCUM_Y_BV, false);
		add(reg);

		reg = cmanager.getRegisterView(CPU.Reg.STATE);
		reg.setProperties("C", REG_C_X_BV, REG_ACCUM_Y_BV, false);
		add(reg);

		cmanager.panelActivate(this);
		cycleview.update();
	}

	@Override
	public void panelDeactivate() {
		cmanager.panelDeactivate();
	}

	@Override
	public String getPanelName() {
		return "Базовая ЭВМ";
	}

	@Override
	public InputRegisterView getNextInputRegister() {
		return (InputRegisterView)cmanager.getRegisterView(CPU.Reg.KEY);
	}

	private void drawOpenBuses(Color color) {
		Graphics g = getGraphics();

		for (BusView bus : openbuses)
			bus.draw(g, color);
	}

	@Override
	public void stepStart() {
		drawOpenBuses(Color.GRAY);
		openbuses.clear();
	}

	@Override
	public void stepFinish() {
		drawOpenBuses(Color.RED);
		cycleview.update();
	}

	@Override
	public SignalListener[] getSignalListeners() {
		return listeners;
	}
}
