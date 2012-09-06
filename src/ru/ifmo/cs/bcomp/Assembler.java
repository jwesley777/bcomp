/*
 * $Id$
 */

package ru.ifmo.cs.bcomp;

import java.util.ArrayList;

/**
 *
 * @author Dmitry Afanasiev <KOT@MATPOCKuH.Ru>
 */

public class Assembler {
	private class Label {
		private String label;
		private Integer addr;
		private int lineno;
		private ArrayList<Command> cmds = new ArrayList<Command>();

		public Label(int lineno, String label) {
			this.lineno = lineno;
			this.label = label;
		}

		public Label(int lineno, String label, int addr) {
			this(lineno, label);
			this.addr = addr;
		}

		public String getLabel() {
			return label;
		}

		public Integer getAddr() {
			return addr;
		}

		public int getLineno() {
			return lineno;
		}

		public void addCommand(Command cmd) {
			cmds.add(cmd);
		}

		public void setAddr(int lineno, int addr) {
			this.lineno = lineno;
			this.addr = addr;

			for (Command cmd : cmds)
				cmd.setArgAddr(addr);
		}
	}

	private class Command {
		private int addr;
		private int cmd;

		public Command(int addr, int cmd) {
			this.addr = addr;
			this.cmd = cmd;
		}

		public Command(int addr, int cmd, Label arg) {
			this(addr, cmd);
			this.cmd += arg.getAddr();
		}

		public int getAddr() {
			return addr;
		}

		public int getCommand() {
			return cmd;
		}

		public void setArgAddr(int addr) {
			cmd += addr;		
		}

	}

	private ArrayList<Label> labels;
	private ArrayList<Label> args;
	private ArrayList<Command> cmds;
	private Instruction[] instrset;

	public Assembler(Instruction[] instrset) {
		this.instrset = instrset;
	}

	public void compileProgram(String program) throws Exception {
		String[] prog = program.replace("\r", "").toUpperCase().split("\n");
		int addr = 0;
		int lineno = 0;

		labels = new ArrayList<Label>();
		args = new ArrayList<Label>();
		cmds = new ArrayList<Command>();

		for (String l : prog) {
			lineno++;

			String[] line = l.trim().split("[#;]+");

			if ((line.length == 0) || line[0].equals(""))
				continue;

			line = line[0].trim().split("[ \t]+");

			if ((line.length == 0) || (line[0].equals("")))
				continue;

			if (line[0].equals("ORG")) {
				if (line.length != 2)
					throw new Exception("Строка " + lineno + ": Директива ORG требует один и только один аргумент");

				addr = Integer.parseInt(line[1], 16);
				continue;
			}

			int col = 0;

			if (line[0].charAt(line[0].length() - 1) == ':') {
				String labelname = line[0].substring(0, line[0].length() - 1);

				if (labelname.equals(""))
					throw new Exception("Строка " + lineno + ": метка не может быть пустой");

				Label label = getLabel(labelname);

				if (label == null) {
					labels.add(new Label(lineno, labelname, addr));
				} else {
					if (label.getAddr() == null)
						label.setAddr(lineno, addr);
					else
						throw new Exception("Строка " + lineno + ": метка " + label.getLabel() +
							" была объявлена в строке " + label.getLineno());
				}	

				col++;
			}

			if (col == line.length)
				continue;

			if (line[col].equals("WORD")) {
				if (col != line.length - 1)
					throw new Exception("Строка " + lineno + ": Директива WORD не требует аргументов");

				Label label = getLabel(addr);

				if (label != null) {
					String labelname = label.getLabel();

					if (!labelname.equals("R") && labelname.charAt(0) != '_')
						args.add(label);
				}

				addr++;
				continue;
			}

			Instruction instr = findInstruction(line[col]);

			if (instr != null) {
				switch (instr.getType()) {
					case ADDR:
						if (col != line.length - 2)
							throw new Exception("Строка " + lineno + ": Адресная команда " + line[col] +
								" требует один и только один аргумент");

						String labelname = line[col + 1];
						int addrtype;

						if (labelname.charAt(0) == '(') {
							if (labelname.charAt(labelname.length() - 1) != ')')
								throw new Exception("Строка " + lineno + ": Нет закрывающей скобки");

							labelname = labelname.substring(1, labelname.length() - 1);
							addrtype = 0x800;
						} else
							addrtype = 0;

						addCommand(lineno, addr, instr.getInstr() + addrtype, labelname);
						break;

					case NONADDR:
						if (col != line.length - 1)
							throw new Exception("Строка " + lineno + ": Безадресная команда " + line[col] +
								" не требует аргументов");

						cmds.add(new Command(addr, instr.getInstr()));
						break;

					case IO:
						if (col != line.length - 2)
							throw new Exception("Строка " + lineno + ": Команда ввода-вывода " + line[col] +
								" требует один и только один аргумент");

						cmds.add(new Command(addr, instr.getInstr() + Integer.parseInt(line[col + 1], 16)));
						break;
				}

				addr++;
				continue;
			}

			if (col != line.length - 1)
				throw new Exception("Строка " + lineno + ": Константа не требует аргументов");

			try {
				int value = Integer.parseInt(line[col], 16);
				cmds.add(new Command(addr++, value));
			} catch (Exception ex) {
				addCommand(lineno, addr++, 0, line[col]);
			}
		}

		for (Label label : labels)
			if (label.getAddr() == null)
				throw new Exception("Строка " + label.getLineno() + ": Не найдена метка " + label.getLabel());
	}

	private Label getLabel(String labelname) {
		for (Label label : labels)
			if (label.getLabel().equals(labelname))
				return label;

		return null;
	}

	private Label getLabel(int addr) {
		for (Label label : labels)
			if ((label.getAddr() != null) && (label.getAddr() == addr))
				return label;

		return null;
	}

	private Instruction findInstruction(String mnemonics) {
		for (Instruction instr : instrset)
			if (instr.getMnemonics().equals(mnemonics))
				return instr;

		return null;
	}

	private void addCommand(int lineno, int addr, int value, String labelname) {
		Label label = getLabel(labelname);

		if (label == null)
			labels.add(label = new Label(lineno, labelname));

		if (label.getAddr() == null) {
			Command cmd = new Command(addr, value);
			cmds.add(cmd);
			label.addCommand(cmd);
		} else 
			cmds.add(new Command(addr, value, label));
	}

	public void loadProgram(CPU cpu) throws Exception {
		for (Command cmd : cmds) {
			cpu.setRegKey(cmd.getAddr());
			cpu.startFrom(ControlUnit.LABEL_ADDR);
			cpu.setRegKey(cmd.getCommand());
			cpu.startFrom(ControlUnit.LABEL_WRITE);
		}

		cpu.setRegKey(getBeginAddr());
		cpu.startFrom(ControlUnit.LABEL_ADDR);
	}

	public String[] getArgs() {
		String[] a = new String[args.size()];
		int i = 0;

		for (Label label : args)
			a[i++] = label.getLabel();

		return a;
	}

	public int getLabelAddr(String labelname) throws Exception {
		Label label = getLabel(labelname);

		if (label == null)
			throw new Exception("Метка " + labelname + " не найдена");

		return label.getAddr();
	}

	public int getBeginAddr() throws Exception {
		return getLabelAddr("BEGIN");
	}

}
