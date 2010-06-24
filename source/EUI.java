import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.ButtonGroup;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import Machine.EControlView;
import Machine.EMachine;

public class EUI extends JApplet
{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	{
		EventQueue.invokeLater(new Runnable()
		{
		public void run()
		{	 
			//Инициализация основных элементов
			tabbedPane = new JTabbedPane();				//Панель закладок
			control = new EControlView(tabbedPane);		//Контроль отрисовки и режимов
			machine = new EMachine(control);			//Машинные объекты
			factory = new ObjectFactoryUI(machine);		//Объекты интерфейса
			
			//Инициализация регистров ввода
			key_register = factory.createKeyRegister();			//Клавишный регистр
			inp1_register = factory.createInputRegister1();		//ВУ 1
			inp2_register = factory.createInputRegister2();		//ВУ 2
			
			//Добавление всех регистров ввода в один массив
			InputRegisterUI[] inpregs = {key_register, inp1_register, inp2_register};
			input_registers = inpregs;
			
			flags = factory.createIOFlags();		//Инициализация флагов		
			
			//Инициализация управляющих компонентов 
			JCheckBox movement_check = factory.createMovementCheckBox();	//Чекбокс сдвигов
			JCheckBox tact_check = factory.createTactCheckBox();			//Чевбокс "Такт"
			JCheckBox memory_check = factory.createMemoryCheckBox();		//Чекбокс "Работа с Памятью МК"
			work = factory.createWorkButton();								//Кнопка "Работа/Останов."
			register_check = factory.createRegisterRadioButtons();			//Кнопки выбора регистра
			
			//Добавление кнопок выбора регистра в группу
			ButtonGroup group = new ButtonGroup();
			for (int i=0; i<register_check.length; i++)
				group.add(register_check[i]);

			//Инициализация компонентов Базовой ЭВМ (Базовая ЭВМ, Работа с ВУ, Работа с МПУ соотв.) 
			final BasePCUI BasePC = new BasePCUI(factory, key_register, movement_check, tact_check, work);
			final IOUnitUI OutputPC = new IOUnitUI(factory, input_registers, flags, movement_check, tact_check, work, register_check);
			final MPUnitUI MicroPC = new MPUnitUI(factory,  key_register,  movement_check, tact_check, memory_check, work);
			
			//Добавление компонентов Базовой ЭВМ на панель закладок
			tabbedPane.addTab("Базовая ЭВМ", BasePC);
			tabbedPane.addTab("Работа с ВУ", OutputPC);
	        tabbedPane.addTab("Работа с МПУ", MicroPC);
	        tabbedPane.setSize(852, 550);
	        tabbedPane.setFocusable(false);
	        
	        //Создание главной панели и размещение на ней панели закладок (для красивого отображения в браузере)
	        final JPanel finalpanel = new JPanel();
			finalpanel.setLayout(null);
	        finalpanel.add(tabbedPane);
	        finalpanel.setFocusable(true);
	        finalpanel.setBackground(Color.WHITE);
	        add(finalpanel);
	        
	        //Создание и добавление слушателей к главной панели
	        finalpanel.addKeyListener(new KeyAdapter() {
	        	public void keyPressed(KeyEvent e) 
		        {    
	        		//Сдвиг указателя выбранного регистра вправо
			        if (e.getKeyCode() == KeyEvent.VK_RIGHT)
	        		{	
			        	for(int i =0; i<input_registers.length; i++)
			        		if(input_registers[i].isActive())
			        			input_registers[i].setPointerPosition(input_registers[i].getPointerPosition() + 1);
		        		
			        	tabbedPane.repaint();
	        		}
			       
	        		//Сдвиг указателя выбранного регистра влево
			        if (e.getKeyCode() == KeyEvent.VK_LEFT)
	        		{
			        	for(int i =0; i<input_registers.length; i++)
			        		if(input_registers[i].isActive())
			        			input_registers[i].setPointerPosition(input_registers[i].getPointerPosition() - 1);
		        		
			        	tabbedPane.repaint();
	        		}
			        
	        		//Изменение бита в выбранном регистре
			        if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN)
	        		{
			        	for(int i =0; i<input_registers.length; i++)
			        		if(input_registers[i].isActive())
			        			input_registers[i].setBit();
		        		
			        	tabbedPane.repaint();
	        		}
			        
			    	//Изменение бита в выбранном регистре на 0
			        if (e.getKeyCode() == KeyEvent.VK_0)
	        		{
			        	for(int i =0; i<input_registers.length; i++)
			        		if(input_registers[i].isActive())
			        			input_registers[i].setBit(false);
		        		
			        	tabbedPane.repaint();
	        		}
			        
			    	//Изменение бита в выбранном регистре на 1
			        if (e.getKeyCode() == KeyEvent.VK_1)
	        		{
			        	for(int i =0; i<input_registers.length; i++)
			        		if(input_registers[i].isActive())
			        			input_registers[i].setBit(true);
		        	
			        	tabbedPane.repaint();
	        		}
			        
			    	//Установка флага "Готовность ВУ 1"
			        if (e.getKeyCode() == KeyEvent.VK_F1)
			        {
			        	flags[0].setFlag();
			        	tabbedPane.repaint();
	        		}
			        
			        //Установка флага "Готовность ВУ 2"
			        if (e.getKeyCode() == KeyEvent.VK_F2)
			        {
			        	flags[1].setFlag();
			        	tabbedPane.repaint();
	        		}
			        
			        //Установка флага "Готовность ВУ 3"
			        if (e.getKeyCode() == KeyEvent.VK_F3)
			        {
			        	flags[2].setFlag();
			        	tabbedPane.repaint();
	        		}
			        
			        //Ввод адреса
			        if (e.getKeyCode() == KeyEvent.VK_F4)
			        {		        	        		
		        		machine.Adress();
	        		}
			        
			        //Запись
			        if (e.getKeyCode() == KeyEvent.VK_F5)
			        {		        		
		        		machine.Record();
	        		}
			        
			        //Чтение
			        if (e.getKeyCode() == KeyEvent.VK_F6)
			        {		        		
	        		}
			        
			        //Пуск
			        if (e.getKeyCode() == KeyEvent.VK_F7)
			        {				        					        	
			        	machine.Start();  	
	        		}
			        
			        //Продолжение
			        if (e.getKeyCode() == KeyEvent.VK_F8)
			        {
				        machine.Continue();
	        		}
			        
			        //Работа/Остановка
			        if (e.getKeyCode() == KeyEvent.VK_F9)
			        {			        			        		
			        	if (machine.GetFlagFac().GetStateOfTumbler().SendData() == 0)
						{
							work.setForeground(Color.BLACK);
							work.setFont(new Font("Courier New", Font.PLAIN, 17));
							work.setText("Остановка");
						}
						else
						{
							work.setForeground(Color.RED);
							work.setFont(new Font("Courier New", Font.PLAIN, 24));
							work.setText("Работа");
						}
		        		
		        		machine.StopWork();
	        		}
		        }
			});
	        
	      //Слушатель нажатия для чекбокса проверки сдвига
			ActionListener movement_listener = new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
				{	
		        	for(int i =0; i<input_registers.length; i++)
		        		if(input_registers[i].isMovement())
		        			input_registers[i].setMovement(false);
		        		else
		        			input_registers[i].setMovement(true);
					
		        	tabbedPane.repaint();
				}
			};
			movement_check.addActionListener(movement_listener);

			//Слушатель нажатия для чекбокса "Такт"
			ActionListener tact_listener = new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
				{	
					if(control.GetTact())
						control.ClearTact();
					else
						control.SetTact();
					
					tabbedPane.repaint();
				}
			};
			tact_check.addActionListener(tact_listener);
			
			//Слушатель нажатия для чекбокса "Работа с памятью МК"
			ActionListener memory_listener = new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
				{	
					if(control.MicroWork())
						control.ClearMicroWork();
					else
						control.SetMicroWork();
					
					tabbedPane.repaint();
				}
			};
			memory_check.addActionListener(memory_listener);
			
			//Слушатель нажатия кнопки "Ввод в КР"
			ActionListener key_listener = new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
				{	
					key_register.setActive(true);
					inp1_register.setActive(false); 
					inp2_register.setActive(false);
					
					tabbedPane.repaint();
				}
			};
			register_check[0].addActionListener(key_listener);
				
			//Слушатель нажатия щелчка по панели
			MouseListener tab_listener = new MouseListener()
			{
				public void mouseClicked(MouseEvent e) 
				{
					key_register.setActive(true);
					inp1_register.setActive(false); 
					inp2_register.setActive(false);
					register_check[0].setSelected(true);
					
					tabbedPane.repaint();	
				}

				public void mouseEntered(MouseEvent e) {}
				public void mouseExited(MouseEvent e) {}
				public void mousePressed(MouseEvent e) {}
				public void mouseReleased(MouseEvent e) {}
			};
			tabbedPane.addMouseListener(tab_listener);
			
			//Слушатель нажатия кнопки "Ввод в ВУ 2"
			ActionListener inp1_listener = new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
				{	
					key_register.setActive(false);
					inp1_register.setActive(true); 
					inp2_register.setActive(false);
					
					tabbedPane.repaint();
				}
			};
			register_check[1].addActionListener(inp1_listener);
			
			//Слушатель нажатия кнопки "Ввод в ВУ 3"
			ActionListener inp2_listener = new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
				{	
					key_register.setActive(false);
					inp1_register.setActive(false); 
					inp2_register.setActive(true);
					
					tabbedPane.repaint();
				}
			};
			register_check[2].addActionListener(inp2_listener);
			
			//Слушатель нажатия кнопки "Работа/Остановка"
			ActionListener work_listener = new ActionListener() 
			{
				public void actionPerformed(ActionEvent e) 
				{
	        		
	        		if (machine.GetFlagFac().GetStateOfTumbler().SendData() == 0)
					{
						work.setForeground(Color.BLACK);
						work.setFont(new Font("Courier New", Font.PLAIN, 17));
						work.setText("Остановка");
					}
					else
					{
						work.setForeground(Color.RED);
						work.setFont(new Font("Courier New", Font.PLAIN, 24));
						work.setText("Работа");
					}
	        		
	        		machine.StopWork();
				}
			};
			work.addActionListener(work_listener);
		}
	});
	}
	
	private JTabbedPane				tabbedPane;
	private	EControlView 			control;
	private EMachine				machine;
	private ObjectFactoryUI 		factory;
	private InputRegisterUI 		key_register;
	private InputRegisterUI 		inp1_register; 
	private InputRegisterUI 		inp2_register;
	private FlagUI[]				flags;
	private InputRegisterUI[]		input_registers;
	private JButton 				work;
	private JRadioButton[]			register_check;

}
