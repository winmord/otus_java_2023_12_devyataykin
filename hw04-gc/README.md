<h3>1 этап</h3>
&emsp;На первом этапе домашнего задания были проведены замеры времени исполнения оригинального кода в зависимости от размера хипа. В Таблице 1 представлены результаты.

| Size (Mb)     | Time                              |
|---------------|-----------------------------------|
| 256           | spend msec:20155, sec:20          |
| 512           | spend msec:15637, sec:15          |
| 1024          | spend msec:13952, sec:13          |
| 1536          | spend msec:13320, sec:13          |
| <h3>2048</h3> | <h3>spend msec:13098, sec:13</h3> |
| 2304          | spend msec:13383, sec:13          |
| 2560          | spend msec:13489, sec:13          |
| 3072          | spend msec:13790, sec:13          |
| 4096          | spend msec:13795, sec:13          |

&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp; Таблица 1

&emsp;Из Таблицы 1 можно сделать вывод, что оптимальный размер хипа, т.е. размер, превышение которого
не приводит к сокращению времени выполнения приложения, составляет 2048 Мбайт.

<h3>2 этап</h3>
&emsp;Вторым этапом была модификация кода, чтобы приложение работало быстро с минимальным хипом.
Были сделаны следующие изменения:
* <code>private final List<Data> listValues</code> в <code>Summator</code> был заменён на простой счётчик <code>dataCounter</code>, поскольку он использовался для подсчёта объектов типа <code>Data</code>. Вследствие этого, объекты не хранятся в памяти и не заполняют хип.
* В классе <code>Summator</code> тип переменных <code>sum</code>, <code>prevValue</code>, <code>prevPrevValue</code>, <code>sumLastThreeValues</code>, <code>someValue</code> был изменён на примитивный, чтобы снизить затраты на Boxing и Unboxing.

Была произведена попытка убрать модификатор <code>final</code> поля <code>value</code> в классе <code>Data</code>, добавить setter и конструктор без параметров. В классе <code>CalcDemo</code> создание объекта типа <code>Data</code> было вынесено за пределы цикла, а внутри цикла остался только вызов setter-a. Но это не привело к приросту производительности, поэтому было принято решение отказаться от этих изменений.

&emsp;В Таблице 2 представлены замеры времени исполнения модифицированного кода в зависимости от размера хипа.

| Size (Mb) | Time                   |
|-----------|------------------------|
| 256       | spend msec:788, sec:0  |
| 512       | spend msec:802, sec:0  |
| 1024      | spend msec:769, sec:0  |
| 1536      | spend msec:780, sec:0  |
| 2048      | spend msec:792, sec:0  |
| 2304      | spend msec:774, sec:0  |
| 2560      | spend msec:773, sec:0  |
| 3072      | spend msec:794, sec:0  |
| 4096      | spend msec:765, sec:0  |

&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp; Таблица 2

&emsp;Из Таблицы 2 можно сделать вывод, что время выполнения приложения перестало зависеть от размера хипа и, следовательно, этот размер можно сделать минимальным.