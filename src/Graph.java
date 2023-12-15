import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import javax.swing.*;

public class Graph extends JFrame {
    public Graph(String title, String age, String team) {
        super(title);
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Полученные строки разбиваем в массивы
        String[] ageArray = age.split(" ");
        String[] teamArray = team.split("  ");

        // Добавления набора данных для графика
        for (int i = 0; i < ageArray.length; i++) {
            dataset.setValue(Double.parseDouble(String.format("%.2f", Double.parseDouble(ageArray[i]))
                            .replace(",", ".")),
                    "Возраст", teamArray[i]);
        }

        // Создания столбчатой диаграммы, принимая заголовок графика,
        // заголовки осей и созданный набор данных
        JFreeChart chart = ChartFactory.createBarChart("Задача 1",
                "Команда", "Возраст", dataset);

        // Создание панели для графика
        ChartPanel panel = new ChartPanel(chart);
        // Установка панели в качестве содержимого фрейма
        setContentPane(panel);
    }
}