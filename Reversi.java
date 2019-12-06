import java.awt.*;
import java.awt.event.*;
import javax.lang.model.util.ElementScanner6;
import javax.swing.*;
//import org.graalvm.compiler.replacements.nodes.UnaryMathIntrinsicNode;

import java.util.*;

class Stone
{
    public final static int black=1;
    public final static int white=2;
    public int obverse;
    Stone()
    {
		obverse = 0;
    }
    
    void setObverse(int color)
	{
		if (color == black || color == white) {
			obverse = color;
		} else {
			System.out.println("black or white");
		}
	}
	
	void Reverse(int s) {
		if(s==black)
			obverse = white;
		else if(s==white)
			obverse = black;
		else
			System.out.println("black or white");
	}
    void paint(Graphics g,Point p,int rad){
			if (obverse == black) {
				g.setColor(Color.black);
				g.fillOval(p.x, p.y, rad * 2, rad * 2);
			}else if(obverse==white){
			g.setColor(Color.white);
			g.fillOval(p.x,p.y,rad*2,rad*2);
	    }
    }
}

class Board {
	Stone st[][] = new Stone[8][8];//stone
	public int num_grid_black;
	public int num_grid_white;
	private Point[] direction = new Point[8];
	public int[][] eval_black = new int[8][8];
	public int[][] eval_white = new int[8][8];

	Board() {
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++)
				st[i][j] = new Stone();//stone syokika

		st[3][3].setObverse(1);
		st[3][4].setObverse(2);
		st[4][3].setObverse(2);
		st[4][4].setObverse(1);

		//bekutoruseisei
		direction[0] = new Point(1, 0);
		direction[1] = new Point(1, 1);
		direction[2] = new Point(0, 1);
		direction[3] = new Point(-1, 1);
		direction[4] = new Point(-1, 0);
		direction[5] = new Point(-1, -1);
		direction[6] = new Point(0, -1);
		direction[7] = new Point(1, -1);
	}

	//checking in board or outside
	boolean isOnBoard(int x, int y) {
		if (x < 0 || 7 < x || y < 0 || 7 < y)
			return false;
		else
			return true;
	}

	//(x,y)から方向dに向かって石をget
	ArrayList<Integer> getLine(int x, int y, Point d) {
		ArrayList<Integer> line = new ArrayList<Integer>();
		int cx = x + d.x;
		int cy = y + d.y;
		while (isOnBoard(cx, cy) && st[cx][cy].obverse != 0) {
			line.add(st[cx][cy].obverse);
			cx += d.x;
			cy += d.y;
		}
		return line;
	}
	
	void change(int x, int y, int s) {//裏返し
		for (int i = 0; i < 8; i++) {
			ArrayList<Integer> l = new ArrayList<Integer>();
			l = getLine(x, y, direction[i]);
			int n = 0;
			int cx = x;
			int cy = y;

			while (n + 1< l.size() && st[cx][cy].obverse == s && l.get(n) != s) {
				n++;
				cx +=direction[i].x;
				cy +=direction[i].y;
				st[cx][cy].Reverse(st[cx][cy].obverse);
			}
		}
	}

	//(x,y)に石をsetした場合に反転できる石の数を数える
	int countReverseStone(int x, int y, int s) {
		if (st[x][y].obverse != 0)//すでに入ってる時
			return -1;
		int cnt = 0;
		for (int d = 0; d < 8; d++) {//8つのdirection
			ArrayList<Integer>line = new ArrayList<Integer>();
			line = getLine(x, y, direction[d]);
			int n = 0;
			while (n < line.size() && line.get(n) != s)
				n++;
			if (1 <= n && n < line.size())
				cnt += n;
		}
		return cnt;//反転できる石の数
	}

	//盤面を評価
	void evaluateBoard() {//裏返せなかったら1を返す、裏返せる時0
		num_grid_black = 0;
		num_grid_white = 0;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				eval_black[i][j] = countReverseStone(i, j, 1);//(i,j)に黒をsetしたら裏返せる数
				if (eval_black[i][j] > 0) {
					num_grid_black++;
				}
				eval_white[i][j] = countReverseStone(i, j, 2);//(i,j)に白をsetしたら裏返せる数
				if (eval_white[i][j] > 0)
					num_grid_white++;
			}
		}
	}

	void paint(Graphics g, int unit_size) {
		int w=640;
		int h=640;
		g.setColor(Color.black);
		g.fillRect(0, 0, 800, 800);

		g.setColor(new Color(0, 85, 0));
		g.fillRect(80, 80, w, h);
		int x1 = (w / 10) * 1;
		int x2 = 720;
		int y1 = 80;
		g.setColor(Color.black);
		for (int i = 0; i < 9; i++) {
			g.drawLine(x1, y1, x2, y1);
			y1 = y1 + 80;
		}
		x1 = 80;
		for (int i = 0; i < 9; i++) {
			g.drawLine(x1, 80, x1, 720);
			x1 += 80;
		}
		x1 = 80 * 3;
		y1 = 80 * 3;
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				g.fillOval(x1 - 4, y1 - 4, 8, 8);
				y1 += 80 * 4;
			}
			x1 += 80 * 4;
			y1 = 80 * 3;
		}

		Point p = new Point();
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				p.x = ((i + 1) * unit_size + unit_size / 10);
				p.y=( (j + 1) * unit_size + unit_size / 10);
				//p.x = (unit_size + (i + 1) + unit_size + (i + 2)) / 2;
				//p.y = (unit_size * (j + 1) + unit_size * (j + 2)) / 2;
				st[i][j].paint(g, p, unit_size * 2 / 5);
			}
		}
	}

	void setStone(int x, int y, int s) {
		st[x][y].setObverse(s);
	}

	int color(int i, int j) {
		return st[i][j].obverse;
	}

	int countStone(int s) {//盤面上s色の石の数
		int r=0;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (st[i][j].obverse == s)
					r++;
			}
		}
		return r;
	}

	//確認用
	void printBoard() {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				 System.out.printf("%2d", st[j][i].obverse);//ok??daekamo
			}
			System.out.println("");
		}
	}

	//盤面の評価結果をコンソールに表示
	void printEval() {
		System.out.println("Black(1):");
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				System.out.printf("%2d", eval_black[j][i]);
			}
			System.out.println("");
		}
		System.out.println("White(2):");
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				System.out.printf("%2d", eval_white[j][i]);
			}
			System.out.println("");
		}
	}//ここまで

}

public class Reversi extends JPanel {
	private static final long serialVersionUID = 1L;
	public final static int UNIT_SIZE = 80;
	static Board board2 = new Board();
	//int x, y;
	public int which = 1;
	private Player[] player = new Player[2];

	public Reversi() {
		setPreferredSize(new Dimension(800, 800));
		addMouseListener(new MouseProc());
		player[0] = new Player(Stone.black, Player.type_human);
		player[1] = new Player(Stone.white, Player.type_computer);
		which = Stone.black;
	}

	public void paintComponent(Graphics g) {
		String msg1 = "";
		board2.paint(g, UNIT_SIZE);
		g.setColor(Color.white);
		if(which==Stone.black)
			msh1 = "black turn";
		else
			msg = "white turn";
		if (player[which - 1].getType() == Player.type_computer)
			msg1 += "(thinking now)";
		String msg2 = "[black:" + board2.countStone(Stone.black) + ",white" + board.countStone(Stone.white) + "]";
		g.drawString(msg1, UNIT_SIZE / 2, UNIT_SIZE / 2);
		g.drawString(msg2, UNIT_SIZE / 2, 19 * UNIT_SIZE / 2);
	}

	void which() {//black or white about turn
		if (which == 1)
			which = 2;
		else if (which == 2)
			which = 1;
	}

	void MessageDialog() {
		int b = board2.countStone(Stone.black);
		int w = board2.countStone(Stone.white);
		String judge;
		if (b == w) {
			judge = "[black:" + b + ",white:" + w + "]draw";
		} else if (b > w) {
			judge = "[black:" + b + ",white:" + w + "]black win";
		} else {
			judge = "[black:" + b + ",white:" + w + "]white win";
		}
		JOptionPane.showMessageDialog(this, judge, "game finished", JOptionPane.INFORMATION_MESSAGE);//result
		System.exit(0);
	}

	void Message(String str) {
		JOptionPane.showMessageDialog(this, str, "infomation", JOptionPane.INFORMATION_MESSAGE);
	}

	class Player {
		public final static int type_human = 0;
		public final static int type_computer = 1;
		private int color;//Stonee.black or Stone.white
		private int type;//type_human or type_computer

		Player(int c, int t) {
			if (c == Stone.black || c == Stone.white)
				color = c;
			else {
				System.out.println("Player's color must be black or white:" + c);
				System.exit(0);
			}
			if (t == type_human || t == type_computer)
				type = t;
			else {
				System.out.println("Player must be human or computer:" + t);
				System.exit(0);
			}
		}

		int getColor() {
			return color;
		}
		int getType() {
			return type;
		}

		Point tactics(Board bd) {//decide
			//left upside
			if (color == Stone.black) {
				for (int i = 0; i < 8; i++) {
					for (int j = 0; j < 8; j++) {
						if (bd.eval_black[i][j] > 0) {
							return (new Point(i, j));
						}
					}
				}
			} else if (color == Stone.white) {
				for (int i = 0; i < 8; i++) {
					for (int j = 0; j < 8; j++) {
						if (bd.eval_white[i][j] > 0) {
							return (new Point(i, j));
						}
					}
				}
			}
			 return (new Point(-1, -1));
		}

		Point nextMove(Board bd, Point p) {
			if(type==type_human)
				return p;
			else if(type==type_computer)
				return tactics(bd);
			else return (new Point(-1, -1));//nennnotame
		}
	}

	class MouseProc extends MouseAdapter {
		public void mouseClicked(MouseEvent me) {
			Point point = me.getPoint();
			int btn = me.getButton();
			System.out.println("(" + point.x + "," + point.y + ")");
			Point gp = new Point();
			gp.x = point.x / UNIT_SIZE - 1;
			gp.y = point.y / UNIT_SIZE - 1;
			if (!board.isOnBoard(gp.x, gp.y))//盤面のout
				return;
			removeMouseListener(this);//itizitekinitomeru
			if (player[which - 1].getType() == Player.type_human) {//ここから人の番
				if ((player[which - 1].getColor() == Stone.black && board2.num_grid_black == 0)
						|| (player[which - 1].getColor() == Stone.white && board2.num_grid_white == 0)) {
					Message("you are pass");
					which();
					repaint();
				}//ここまで置けなibaai 
				 else if ((player[which - 1].getColor() == Stone.black && board.eval_black[gp.x][gp.y] > 0)
						|| (player[which - 1].getColor() == Stone.white && boaard.eval_white[gp.x][gp.y] > 0)) {
					Point nm = player[which - 1].nextMove(board2, gp);
					if (which == 1 && btn == MouseEvent.BUTTON1) {
						int x, y;
						x=point.x;
						y = point.y;
						x = x / 80 - 1;
						y = y / 80 - 1;
						board2.evaluateBoard();
						if (0 <= x && x < 8 && 0 <= y && y < 8) {
							if (board2.eval_black[x][y] < 1) {
								System.out.println("Dont put here");
							} else {
								nm.x=x;
								nm.y=y;
								if (board2.num_grid_black >= 1)
									Message("white turn");
								else {
									which();
									Message("You are pass");
								}
							}
						}
					}
					else if (btn == MouseEvent.BUTTON3 && which == 2) {//right
						int x, y;
						x = point.x;
						y = point.y;
						x = x / 80 - 1;
						y = y / 80 - 1;
						if (0 <= x && x < 8 && 0 <= y && y < 8) {
							if (board2.eval_white[x][y] < 1) {
								System.out.println("Dont put here");
							} else {
								nm.x = x;
								nm.y = y;
								if (board2.num_grid_white >= 1)
									Message("black turn");
								else {
									which();
									Message("You are pass");
								}
							}
						}
					}
					board2.change(nm.x, nm.y, player[which - 1].getColor());//nmに石を置ku
					which();
					repaint();
					//game finish confirm
					if (board.num_grid_black == 0 && board.num_grid_white == 0)
						MessageDialog();
				}
				if (player[which - 1].getType() == Player.type_human)
					addMouseListener(this);
			}
			if (player[which - 1].getType() == Player.type_computer) {
				thread th = new TacticsThread();
				th.start();
			}

			//count
			int black_cnt = 0;
			int white_cnt = 0;
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					if (board2.st[i][j].obverse == 1) {
						black_cnt++;//黒石の数
					} else if (board2.st[i][j].obverse == 2) {
						white_cnt++;//白石の数
					}
				}
			}
			//repaint();
			if (board2.num_grid_black < 1 && board2.num_grid_white < 1) {
				MessageDialog();
			}
		}
	}

	class TacticsThread extends Thread {
		public void run() {
			try{
				Thread.sleep(200);//wait 2sec
				Point nm=player[which-1].nextMove(board,new Point(-1,-1));
				if(nm.x==-1&&nm.y==-1){
					Message("enemy is pass");
				}else{
					board2.change(nm.x,nm.y, player[which-1].getColor());
				}
				which();
				repaint();
				addMouseListener(new MouseProc());//mouse restart
				//game end confirm
				if(board2.num_grid_black==0&&board.num_grid_white==0)
				MessageDialog();
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
	}
	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.getContentPane().setLayout(new FlowLayout());
		f.getContentPane().add(new Reversi());
		f.pack();
		f.setResizable(false);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
}
