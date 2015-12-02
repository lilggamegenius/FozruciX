import javax.swing.*;
import java.awt.*;

/**
 * Created by ggonz on 11/4/2015.
 * A debug window to monitor certain variables
 */
public class DebugWindow extends JFrame {
    // Define constants, variables, and labels
    private static final int WIDTH = 0x2CC;
    private static final int HEIGHT = 0xA0;
    private JTextField currentNickTF, messageLabelTF, currDMTF, myPlayerNameTF, myPlayerHPTF, myPlayerXPTF, myFamiliarTF, myFamiliarHPTF, myFamiliarXPTF;

    public DebugWindow(String botNick) {
        JLabel currentNickL, messageLabelL, currDML, myPlayerNameL, myPlayerHPL, myPlayerXPL, myFamiliarL, myFamiliarHPL, myFamiliarXPL;
        setTitle(botNick);

        currentNickL = new JLabel("Currently Registered User", SwingConstants.LEFT);
        messageLabelL = new JLabel("Last message", SwingConstants.LEFT);
        currDML = new JLabel("Current Dungeon master", SwingConstants.LEFT);
        myPlayerNameL = new JLabel("My username", SwingConstants.LEFT);
        myPlayerHPL = new JLabel("HP", SwingConstants.LEFT);
        myPlayerXPL = new JLabel("XP", SwingConstants.LEFT);
        myFamiliarL = new JLabel("Familiar", SwingConstants.LEFT);
        myFamiliarHPL = new JLabel("Familiar HP", SwingConstants.LEFT);
        myFamiliarXPL = new JLabel("Familiar XP", SwingConstants.LEFT);

        currentNickTF = new JTextField(10);
        messageLabelTF = new JTextField(10);
        currDMTF = new JTextField(10);
        myPlayerNameTF = new JTextField(10);
        myPlayerHPTF = new JTextField(10);
        myPlayerXPTF = new JTextField(10);
        myFamiliarTF = new JTextField(10);
        myFamiliarHPTF = new JTextField(10);
        myFamiliarXPTF = new JTextField(10);

        //Create Grid layout for window
        Container pane = getContentPane();
        pane.setLayout(new GridLayout(9, 2));

        super.setAlwaysOnTop(true);

        pane.add(currentNickL);
        pane.add(currentNickTF);

        pane.add(messageLabelL);
        pane.add(messageLabelTF);

        pane.add(currDML);
        pane.add(currDMTF);

        pane.add(myPlayerNameL);
        pane.add(myPlayerNameTF);

        pane.add(myPlayerHPL);
        pane.add(myPlayerHPTF);

        pane.add(myPlayerXPL);
        pane.add(myPlayerXPTF);

        pane.add(myFamiliarL);
        pane.add(myFamiliarTF);

        pane.add(myFamiliarHPL);
        pane.add(myFamiliarHPTF);

        pane.add(myFamiliarXPL);
        pane.add(myFamiliarXPTF);

        setSize(WIDTH, HEIGHT);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setVisible(true);


    }

    public void setCurrentNick(String nick) {
        currentNickTF.setText(nick);
    }

    public void setMessage(String message) {
        messageLabelTF.setText(message);
    }

    public void setCurrDM(String DM) {
        currDMTF.setText(DM);
    }

    public void setPlayerName(String DNDName) {
        myPlayerNameTF.setText(DNDName);
    }

    public void setPlayerHP(String HP) {
        myPlayerHPTF.setText(HP);
    }

    public void setPlayerXP(String XP) {
        myPlayerXPTF.setText(XP);
    }

    public void setFamiliar(String Familiar) {
        myFamiliarTF.setText(Familiar);
    }

    public void setFamiliarHP(String HP) {
        myFamiliarHPTF.setText(HP);
    }

    public void setFamiliarXP(String XP) {
        myFamiliarXPTF.setText(XP);
    }

    public void setNick(String botNick) {
        setTitle(botNick);
    }

}

class DrawWindow extends Component {
    int map_size;
    int[][] map;
    Point currentPoint;

    public DrawWindow(int[][] map, int map_size, Point currentPoint) {
        this.map_size = map_size;
        this.map = map;
        this.currentPoint = currentPoint;
    }

    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        int dx = 0;
        int dy = 0;
        int scale = 5;
        g2d.scale(scale, scale);
        try {
            while (dy < map_size) {
                while (dx < map_size) {
                    if (dx == currentPoint.x && dy == currentPoint.y) {
                        g2d.setColor(Color.RED);
                    } else if (map[dx][dy] == 0) {
                        g2d.setColor(Color.CYAN);
                    } else if (map[dx][dy] == 1) {
                        g2d.setColor(Color.BLUE);
                    } else {
                        g2d.setColor(Color.MAGENTA);
                    }
                    g2d.drawLine(dx, dy, dx, dy);
                    dx++;
                    Thread.sleep(10);                 //1000 milliseconds is one second.
                }
                dy++;
                dx = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
