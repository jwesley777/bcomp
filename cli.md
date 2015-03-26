# Эмулятор [БЭВМ](bcomp.md) для командной строки #



# Способы запуска #

  1. Скачать [JAR-архив](http://helios.cs.ifmo.ru/bcomp/bcomp.jar) и запустить эмулятор командой:
```
java -jar -Dmode=cli bcomp.jar
```
  1. Для unix-подобных систем дополнительно скачать [shell скрипт](https://bcomp.googlecode.com/svn/trunk/bcomp-ui/src/main/resources/bcomp) и запустить его командой:
```
bcomp -c
```
  1. Для ОС семейства Windows дополнительно скачать [bat скрипт](https://bcomp.googlecode.com/svn/trunk/bcomp-ui/src/main/resources/cli.bat) и запустить его командой:
```
cli
```
  1. Зайти на сервер кафедры ВТ helios и выполнить команду:
```
bcomp -c
```

# Список команд #
| **Команда** | **Короткая форма** | **Описание** |
|:-------------------|:--------------------------------|:---------------------|
| help | h | Вывости подсказку по командам |
| address | a | Пультовая операция `Ввод адреса` |
| write | w | Пультовая операция `Запись` |
| read | r | Пультовая операция `Чтение` |
| start | s | Пультовая операция `Пуск`. Будет выполнена в фоновом режиме если указана последней в строке |
| continue | c | Пультовая операция `Продолжить`. Будет выполнена в фоновом режиме если указана последней в строке |
| run | ru | Переключение режима Работа/Останов |
| clock | cl | Переключение режима потактового выполнения |
| maddress | ma | Переход на микрокоманду |
| mwrite | mw | Запись микрокоманды |
| mread | mr | Чтение микрокоманды |
| io _`[`addr `[`value`]``]`_ | i | Вывод состояния всех ВУ/указанного ВУ/запись `value` в ВУ |
| flag _addr_ | f | Установка флага готовности указанного ВУ |
| asm | as | Ввод программы на [ассемблере](asm.md) |
| {exit|quit} | {e|q} | Выход из эмулятора |
| value |  | Ввод шестнадцатеричного значения в клавишный регистр |
| label |  | Ввод адреса метки в клавишный регистр |

# Возможности эмулятора #
  1. Автоматическая генерация таблиц трассировки для программ и микропрограмм в точном соответствии с требованиями методички.
  1. Возможность работы с перенаправлением ввода для автоматического выполнения заготовленного сценария работы.
  1. Возможность запуска программы в фоновом режиме для, например, выполнения лабораторных работ, связанных с вводом-выводом.
  1. В одной строке может быть несколько команда, которые будут обработаны в порядке их указания.
  1. Если очередная команда начинается с символа `#`, то весь остаток строки игнорируется.

# Микропрограммы #
  1. [Исходная микропрограмма](BaseMicroProgram.md) используется по умолчанию.
  1. [Оптимизированная микропрограмма](OptimizedMicroProgram.md) используется при указании ключа `-Dmp=optimized`.
  1. [Расширенная микропрограмма](ExtendedMicroProgram.md) используется при указании ключа `-Dmp=extended`.

# Ограничения и известные проблемы #
  1. Последовательность интерпретации эмулятором значений:
    * команда, если значение является началом команды, например `A`, `AD` и `ADD` будут восприняты как пультовая операция `Ввод адреса`;
    * шестнадцатеричное значение, если значение имеет корректный формат и состоит из шестнадцатеричных цифр, например `ADDE`;
    * имя метки, например `ADDZ`.
  1. Так как пультовые операции `Ввод адреса`/`Запись`/`Чтение` представляют собой микропрограммы, при работе в потактовом режиме они останавливают своё выполнение после первой микрокоманды. Для полного выполнения пультовой операции необходимо выключать потактовый режим или выполнять все такты микропрограммы командой `continue`.
  1. При отключенном потактовом режиме состояние регистров выводится только после достижения микрокоманды, останавливающей БЭВМ. Для получения таблицы трассировки программу необходимо выполнять в режиме останов.
  1. Для всех пультовых операций выводится состояние регистров после выполнения этих операций.
  1. Обработка ошибок ввода находится в зачаточном состоянии и весьма ограничена.
  1. В случае зацикливания программы, запущенной не в фоновом режиме, нет возможности штатно остановить её выполнение.
  1. Запрещено выполнять пультовые операции при запущенной в фоновом режиме программе.
  1. При запуске программы в фоновом режиме устанавливается неотключаемая задержка между тактами 1мс.
  1. Ведение истории выполненных команд не предусмотрено, отсутствует возможность вызова предыдущих команд.

# Примеры использования #
## Сгенерировать таблицу трассировки для первой ЛР ##
```
018 a F200 w 4021 w 1022 w 3024 w F200 w 4023 w 4024 w 3024 w F000 w
DEAD w BEEF w ACDC w 018 a s
c c c c c c c c c
```
## Сгенерировать таблицу трассировки для команды 7XXX: ОП(XXX) -> А, 1 -> C ##
```
B0 ma 0100 mw 40F5 mw 8390 mw 010 a 7010 w 010 a s clock
c c c c c c c c c c c c c c c c c c c c c c
```
## Выполнить трассировку пультовой операции `Ввод адреса` ##
```
010 clock a c c c
```
## Используя [ассемблер](asm.md) ввести и выполнить программу, складывающую два числа ##
```
asm
ORG	010
BEGIN:	CLA
	ADD X
	ADD Y
	MOV R
	HLT

X:	WORD	?
Y:	WORD	?
R:	WORD	?
END
X a DEAD w Y a BEEF w BEGIN a run
s
```
## Используя [ассемблер](asm.md) ввести и выполнить программу, которая раз в секунду увеличивает содержимое аккумулятора, а по запросу от ВУ1 выводит накопленное значение ##
```
asm run start
ORG	010
BEGIN:	CLA
	INC
	OUT	0
LOOP:	TSF	0
	BR	TSF1
	INC
	CLF	0
TSF1:	TSF	1
	BR	LOOP
	OUT	1
	CLF	1
	BR	LOOP
END
flag 1
io 1
```