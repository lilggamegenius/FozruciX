import com.google.common.primitives.Doubles;

/**
 * Created by ggonz on 11/11/2015.
 */

public class SineWaveTest {

    public static void main(String[] args){
        final int CHARS = 20;
        double max, min;
        double[] ans = new double[CHARS];
        int index = 0;

        double mathNum = -1;
        while(index < ans.length){
            ans[index] = (Math.sin(mathNum*Math.PI)*100);
            index ++;
            mathNum += 0.1;
        }

        index = 0;

        while(index < ans.length){
            max = Doubles.max(ans);
            min = Doubles.min(ans);

            int percentBase = (int) (max - min);

            int lines = 0;

            index = 0;
            while (index < CHARS){
                double num = Math.abs(Math.round((ans[index] / percentBase) * 100));
                if(num <= 25){
                    ans[index] = 0;
                } else if(num > 25 && num <= 50) {
                    ans[index] = 1;
                } else if(num > 50 && num <= 75) {
                    ans[index] = 2;
                } else if(num > 75 && num <= 100) {
                    ans[index] = 3;
                }

                index++;
            }

            while (lines < 4){
                index = 0;
                String line = "";
                while(index < CHARS){
                    if(ans[index] == lines){
                        line += "*";
                    } else {
                        line += " ";
                    }
                    index++;
                }
                System.out.println(line);
                lines ++;
            }
        }
    }
}
