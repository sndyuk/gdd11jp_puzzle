package com.sndyuk.puzzle;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.sndyuk.puzzle.digdag.DigDagTask;
import com.sndyuk.puzzle.digdag.dfs.DigDagDfs;
import com.sndyuk.puzzle.parts.Board;
import com.sndyuk.puzzle.parts.CodeFactory;
import com.sndyuk.puzzle.parts.Command;
import com.sndyuk.puzzle.parts.Dag;
import com.sndyuk.puzzle.parts.History;
import com.sndyuk.puzzle.util.CmdCounter;

public final class Main {

	private static final ExecutorService SERVICE = Executors
			.newFixedThreadPool(1);

	public static void main(String[] args) throws Exception {

		String inputPath = args[0];
		String outputPath = args[1];
		int startIndex = Integer.parseInt(args[2]);
		int endIndex = Integer.parseInt(args[3]);
		String validationPath = args[4];

		CmdCounter cmdTotal = new CmdCounter();

		int zU = 0, zR = 0, zD = 0, zL = 0;

		int i = 0;
		int cnt = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(inputPath));
				BufferedWriter bw = new BufferedWriter(new FileWriter(
						outputPath));
				BufferedReader hisr = new BufferedReader(new FileReader(
						validationPath))) {
			String line = null;

			String[] cmdTotalArr = br.readLine().split(" ");
			zL = Integer.parseInt(cmdTotalArr[0]);
			zR = Integer.parseInt(cmdTotalArr[1]);
			zU = Integer.parseInt(cmdTotalArr[2]);
			zD = Integer.parseInt(cmdTotalArr[3]);

			System.out.println(br.readLine());

			long total = 0;
			while ((line = br.readLine()) != null) {
				i++;

				String[] arr = line.split(",");
				int w = Integer.parseInt(arr[0]);
				int h = Integer.parseInt(arr[1]);
				String puzzle = arr[2];

				if (i > endIndex) {
					break;
				}
				String hisLine = hisr.readLine();

				if (hisLine != null && hisLine.length() > 0) {

					cmdTotal.addAll(hisLine);

					if (validate(w, h, puzzle, hisLine)) {
						System.out.println("skip: " + i);
						bw.append('\n');
						continue;
					}
				}
				if (i < startIndex) {
					System.out.println("skip: " + i);
					bw.append('\n');
					continue;
				}

				long start = System.currentTimeMillis();
				cnt++;

				String result = null;
				try {
					Future<String> future = SERVICE.submit(new DigDagTask(w, h,
							puzzle, SERVICE));
					result = future.get();

					if (result == null || !validate(w, h, puzzle, result)) {
						System.err.println("unsolved!");
						bw.append('\n');
						continue;
					}

					System.out.println(result);

					bw.append(result);
					bw.append('\n');
					bw.flush();

					CmdCounter cmdCount = new CmdCounter();
					cmdCount.addAll(result);
					cmdTotal.addAll(result);

					System.out.println("#: " + i);
					System.out.println("U: " + cmdCount.getU());
					System.out.println("R: " + cmdCount.getR());
					System.out.println("D: " + cmdCount.getD());
					System.out.println("L: " + cmdCount.getL());

				} catch (Exception e) {
					e.printStackTrace();
					bw.append('\n');
					continue;

				} finally {
					long end = System.currentTimeMillis();
					long elapsed = (end - start) / 1000;
					total += elapsed;
					System.out.println("elapsed: " + elapsed + " sec");
					System.out.println("average: " + total / cnt + " sec");

					System.out
							.println("ALL U: " + cmdTotal.getU() + " / " + zU);
					System.out
							.println("ALL R: " + cmdTotal.getR() + " / " + zR);
					System.out
							.println("ALL D: " + cmdTotal.getD() + " / " + zD);
					System.out
							.println("ALL L: " + cmdTotal.getL() + " / " + zL);
				}
			}
		} finally {
			SERVICE.shutdownNow();
			
			System.out
					.println("ALL U: " + cmdTotal.getU() + " / " + zU);
			System.out
					.println("ALL R: " + cmdTotal.getR() + " / " + zR);
			System.out
					.println("ALL D: " + cmdTotal.getD() + " / " + zD);
			System.out
					.println("ALL L: " + cmdTotal.getL() + " / " + zL);
		}
	}

	public static void main3(String[] args) throws Exception {
//
		 int w = 6;
		 int h = 4;		
//		 String puzzle = "1236BC7=5N4AD=J=9FKL0HIM";
//		 String puzzle = "2356BC1=0N4A7=J=9FDKLHIM";
		 String puzzle = "123FBC7=A69HD=5=LIJK04NM";
//		 // 12346A7=FINBD=H=5CJK0ML9
//		 int w = 6;
//		 int h = 6;
//		 String puzzle = "=2345678K==CF90G=IPJ=MNOED===UVXWQYZ";
//		 
		 // =93450
		 // K72==6
		 // VPF8=N
		 // DJ=GMC
		 // XE===I
		 // WQYZUO
		 
		 
		
//		 int w = 6;
//		 int h = 6;		
//		 String puzzle = "=2738CEF6==I5049GHKD====J=RSTUPVWXYZ"; // 2
		
//		 int w = 6;
//		 int h = 4;		
//		 String puzzle = "94M0==K83CIL27=B=N1DEJAG";
		 // LLLULUUUURRDDRDDLDRDDLLURRRUURULLLULLDDDDRDR
//		 int w = 4;
//		 int h = 6;
//		 String puzzle = "187F0C4D2A5B3=GK6==N9HLM";
//		 String puzzle = "31270A8FD964H=C5L==BMNKG";
//		 UUUURDDDDLULUUULLDDRDRDRUUUUURDDLULLLDDDRRRRDLLLUURUULLDDRRRDDR
		
		 
//		 int w = 6;
//		 int h = 6;
//		 String puzzle = "=823BUP7=4CHJ==6INLRGO5ZV0KA=YWDSQMX";
				 
		 
		 // 6,5,438C5I12=GLA7=Q6OMD=PRHBJ0SFNT
		// int w = 5;
		// int h = 6;
		// String puzzle = "12=E4D9HIF8=GN576LOABMTPKQSR0J";
		// UUUURDDDDLULUUULLDDRDRDRUUUUURDDLULLLDDDRRRRDLLLUURUULLDDRRRDDR
		// U: 18
		// R: 14
		// D: 18
		// L: 13

//		 int w = 4;
//		 int h = 6;
//		 String puzzle = "48KG3C216=7N9D5B=0=F=EIM";
		// ULUURRDRDDDLLUURURDLUULLDDRDDRRUUULULURDRULLDRURDLDRUULLLDRRDDRUUULLLDDDRDDRRUUUUULLDLDDRDDRR
		// URRDDLLUURRULDLDDRRUULUUURDLDRULURDLDDRULDLDDRRUUULDRUULLLDDRRRUULULDRDRUULLLDRRULLDRRRULDLURDDDLDDRR
		// ULUURRRDDDDLLUURURULLLDDRRRULDLDDRRUULUURULLDRDRUULLLDRRRDLUULLDDDRRRULDRUULDDLDDRRUUUUULLDLDDRDDRR
		// 48KG
		// 3C21
		// 6=7N
		// 9D5B
		// =0=F
		// =EIM

		// 48KG
		// 3C21
		// 6=7N
		// 9D5B
		// =E=0
		// =IMF

		// 1234
		// 5678
		// 9=BC
		// DEFG
		// =I=K
		// =MN0

		// int w = 6;
		// int h = 4;
		// String puzzle = "492F6A1=G3C57=LMHBDJ0KNI";

		// int w = 6;
		// int h = 3;
		// String puzzle = "239=56B=0AGC7D1EFH";
		// DRULDLLUURRDDLLUURRDDLLUURRDRDLLLUURRDDLLUURRDRDRULLDLLUURRDDRRR

		// int w = 6;
		// int h = 6;
		// String puzzle = "71A09=D=4G3BPF52=CWJ===IKQRSZU=EX=TO";

		// 71A09=
		// D=4G3B
		// PF52=C
		// WJ===I
		// KQRSZU
		// =EX=TO
		//

//		int w = 6;
//		int h = 4;
//		String puzzle = "81359=D27BCAE0===4=KLMNI";

		// 81359=
		// D27BCA
		// E0===4
		// =KLMNI

		// 12345=
		// 80CA9I
		// B7===N
		// =DEKLM
		// LUURDRRRRDDLLLLULURRRRR
		
		// XXX
//		 int w = 4;
//		 int h = 6;
//		 String puzzle = "187F0C4D2A5B3=GK6==N9HLM";

		// 187F2C4D3A5B6=G09==KHLMN
		// 187F
		// 2C4D
		// 63AB
		// 0=5G
		// 9==K
		// HLMN

		// 1234
		// 5678
		// 9ABC
		// D=FG
		// H==K
		// LMN0

		// int w = 6;
		// int h = 6;
		// String puzzle = "41M69A8KSG5C023DTR7JEBNXP=WOIUVYZHFL";

//		 int w = 4;
//		 int h = 4;
//		 String puzzle = "82C=9B1E05736ADF";
		//
		// int w = 6;
		// int h = 6;
		// String puzzle = "40G9E31DJA56872F=BP===NZVWR=CUXQYTOI";
		// 5700000 : 80
		// LDDDDRDRRRRULURDDLLLLLUUUURRULLDRDLUURRRRRDDDDLURDDLUURDDLLLLLUUUURURRRDLLDLURULDRDLURDRULURDRRDDDLDR
		// DDLURRDLURULDDLURDRUULDRDLLDDRDRRRRULURDDLLLLLUUUUURRRRRDDDDLURDDLUURDDLLLLLUUURUURRRDLLULDLDRRULDRRULURDRRDDDLDR

		// U: 22
		// R: 28
		// D: 27
		// L: 24
//		 int w = 4;
//		 int h = 4;
//		 String puzzle = "32465871FAC0=9BE";
		// LULDDRRULURULLLDRRULDDLURRRDLLDRULLUURDRRDD
		// LULULDDRDRUURULDLLDRRDRULLUURDLLURRDRDLLDRULURRDD
		// U: 9
		// R: 12
		// D: 10
		// L: 12

		// int w = 5;
		// int h = 4;
		// String puzzle = "617FAB0825HCEJ34G9DI";
		// 17100000 : 25
		// LLURUULLDRDLDRRUULULDDRDL
		// DLDRULUURRRDRDLLLURURDLDRURULDLDDRRULLULURRDDLDRR
		// U: 10
		// R: 15
		// D: 12
		// L: 12

//		 int w = 3;
//		 int h = 6;
//		 String puzzle = "17=42=580HFEABD9GC";
		// 2900000 : 23
		// DDLULDDRUURDDLUUUUULDRDLURDRUULDRDDDLUULURDLUUU
		// DDLULDDRULURRDLULDRURULDRDDLUUUUULDRDLUURDDRDDD
		// LDDRULDLURDLDRULURUUULDRDDDRULULUURDDRDDDLUURDLDR
		// LDDRULDLURDLDRULURUUULDRDDDRULULUURDDRDDDLUURDLDR
		// LLDRDLUURDDLURDLDRULURDRULLDRRDLLURUUUULDRDLUURDDRDDLLDRR
		// LDDLURDLDRULURDRULUUULDRDLDDRRDLUULUUURDDRDDLDR
		// DLDRUULDDLURDDLURDRULDLUURRDLUUUULDRDLUURDDRDDD
		// U: 17
		// R: 7
		// D: 14
		// L: 9

		// LUULDRDDLDDRRUULDLURDDRUULULUURDDRDLDDRUULLDRDR
		// U: 12
		// R: 10
		// D: 15
		// L: 10
		//
		// LUULDRDDLDDRRUULDLURDDRUULULUURDDRDLDDRUULLDRDR
		// U: 12
		// R: 10
		// D: 15
		// L: 10
		//
		// OK
		// int w = 4;
		// int h = 6;
		// String puzzle = "94827601A3BCD5JGMEFNHLKI";
		// solved:
		// RULDLURDLLURRRDDLUURDLDLUURDDDDDRULUURUULDLDDLURULDRURDLLURDRULDRURDLLULDRDLURDRUURDLLDLURDDDRRUULDDRUUUUULDRDDDDLUURDDLLULDRRULDLUUURDLDRURDLLDRRUULDRDLURDR
		// RULLDRRULLDLURRDLDDRDRDLURULLDLDRURUUULDLURURDDDRDD
		// U: 41
		// R: 36
		// D: 45
		// L: 35
		//
		// // OK
		// int w = 3;
		// int h = 3;
		// String puzzle = "123456078";

		// OK
		// int w = 3;
		// int h = 3;
		// String puzzle = "168452=30";
		// ULDRUULDDRUULDDR
		// U: 5
		// R: 3
		// D: 5
		// L: 3

		// solved: LUURDDLUDLUURDLU
		// U: 6
		// R: 2
		// D: 4
		// L: 4

		// OK
		// // 4,4,
		// int w = 4;
		// int h = 4;
		// String puzzle = "32465871FAC0=9BE";
		// solved: LULDDRRULURULLLDRRULDURDLLLURRULDRRDDLULLUU
		// U: 11
		// R: 10
		// D: 8
		// L: 14

		// 160
		// 42
		// =53

		// unreacheable!
		// String puzzle = "12=456=08";
		// 9482
		// 7601
		// A3BC
		// D5JG
		// MEFN
		// HLKI

		// int w = 3;
		// int h = 3;
		// String puzzle = "867254301";
		// UURDLLDRRULLURRDLDLUURDDLUURRDD
		// U: 8
		// R: 8
		// D: 8
		// L: 7

		boolean bfs = true;
		if (bfs) {
			try {
				Future<String> future = SERVICE.submit(new DigDagTask(w, h,
						puzzle, SERVICE));
				String result = future.get();
				System.out.println(result);
				if (validate(w, h, puzzle, result)) {
					System.err.println("unsolved!");
					return;
				}

				CmdCounter cmdCount = new CmdCounter();
				cmdCount.addAll(result);

				System.out.println("U: " + cmdCount.getU());
				System.out.println("R: " + cmdCount.getR());
				System.out.println("D: " + cmdCount.getD());
				System.out.println("L: " + cmdCount.getL());
			} finally {
				SERVICE.shutdownNow();
			}
		} else {
			String result = solveByDFS(w, h, puzzle);

			System.out.println(result);
			if (validate(w, h, puzzle, result)) {
				System.err.println("unsolved!");
				return;
			}

			CmdCounter cmdCount = new CmdCounter();
			cmdCount.addAll(result);

			System.out.println("U: " + cmdCount.getU());
			System.out.println("R: " + cmdCount.getR());
			System.out.println("D: " + cmdCount.getD());
			System.out.println("L: " + cmdCount.getL());
		}

	}

	private static boolean validate(int w, int h, String puzzle, String cmds) {
		if (puzzle == null) {
			return false;
		}
		try {
			CodeFactory defaultCf = CodeFactory.getDefaultCodeFactory();
			Board board = Board.Utils.createBoard(w, h, puzzle.toCharArray(),
					defaultCf);
			Dag dag = new Dag(board);
			for (char c : cmds.toCharArray()) {
				dag.forward(Command.valueOf(c));
			}
			if (Board.Utils.getAllDistance(board) != 0) {
				System.err.println("validation error");
				return false;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			// ignore
			System.err.println("validation error");
			return false;
		}
	}

	private static String solveByDFS(int w, int h, String puzzle) {

		CodeFactory defaultCf = CodeFactory.getDefaultCodeFactory();
		Board board = Board.Utils.createBoard(w, h, puzzle.toCharArray(),
				defaultCf);

		try (DigDagDfs digdag = new DigDagDfs(board)) {

			History his = null;
			while (digdag.hasNext()) {
				digdag.next().get(0);

			}
			if (digdag.getGoal() == null) {
				return null;
			}

			StringBuilder cmdSb = new StringBuilder();
			for (Iterator<Command> cmdIterator = History.Utils
					.createCommandIterator(his); cmdIterator.hasNext();) {
				cmdSb.append(cmdIterator.next());
			}
			return cmdSb.reverse().toString();
		}
	}

	public static void main6(String[] args) throws Exception {
		
		 int w = 6;
		 int h = 4;
		 String puzzle = "1023BC7=95IHD=6=ML4FAKNJ";

		 // RRDLDDLLUUURRRDLDDLLUUURRRDLDDRRRUULLLULLDDDRRRRRUULLLDDRRRULULLDDRRRULULLULLDDDRR
		 // RRDLDDLLUUURRRDLDDLLUUURRRDLDDRRRUULLLULLDDDRRRRRUULLLDDRRRULULLDDRRRULULLULLDDDRR
		 // RRDDLLLLUUURRDRRDDLLUUULLDDDRRRRUUURDDLURULDLLURRDLULDDDRRUUULLDRRRDLDLLUURRDDRUUULLLLLDDDRRRRR
		 
		 // RRDLDDLLUUURRRRRDDLDRUULLLDDRRURUULLLDDDLLUUURRDDDRRUULULLLDDDRRUURRDDLLUUULLDDDRRUUURDRRDDLURULULLDDDRRUULLDDRRRUUULDLLDDRRUULULDRURDDDLLUURULDRRDRD  
		 // RRDLDDLLUUURRRDLDDLLUUURRRDLDDRRRUULLLULLDDDRRRRRUULLLDDRRRULULLDDRRRULULLRRDDLLLLUUURRDRRDDLLUUULLDDDRRRRUUURDDLURULDLLURRDLULDDDRRUUULLDRRRDLDLLUURRDDRUUULLLLLDDDRRRRR
		if (validate(w, h, puzzle,
				"RRDLDDLLUUURRRRRDDLDRUULLLDDRRURUULLLDDDLLUUURRDDDRRUULULLLDDDRRUURRDDLLUUULLDDDRRUUURDRRDDLURULULLDDDRRUULLDDRRRUUULDLLDDRRUULULDRURDDDLLUURULDRRDRD")) {
			System.out.println("validate true");
		} else {
			System.err.println("validate false");
		}
	}
	
	public static void maina3(String[] args) throws Exception {

		String inputPath = args[0];
		String outputPath = args[1];
		int startIndex = Integer.parseInt(args[2]);
		int endIndex = Integer.parseInt(args[3]);
		String validationPath = args[4];

		CmdCounter cmdTotal = new CmdCounter();

		int zU = 0, zR = 0, zD = 0, zL = 0;

		int i = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(inputPath))) {
			String line = null;

			String[] cmdTotalArr = br.readLine().split(" ");
			zU = Integer.parseInt(cmdTotalArr[0]);
			zR = Integer.parseInt(cmdTotalArr[1]);
			zD = Integer.parseInt(cmdTotalArr[2]);
			zL = Integer.parseInt(cmdTotalArr[3]);

			System.out.println(br.readLine());

			while ((line = br.readLine()) != null) {
				i++;

				String[] arr = line.split(",");
				int w = Integer.parseInt(arr[0]);
				int h = Integer.parseInt(arr[1]);
				String puzzle = arr[2];

				if (validate(w, h, puzzle,
						"LDLDRURULLDDRURDDLURDDDLLLUUURRRDDDLLLUUUURDRUURDLULDRDLLDDDRRR")) {
					System.out.println("validate true: " + i);

					break;
				}
			}
		} finally {

			System.out.println("ALL U: " + cmdTotal.getU() + " / " + zU);
			System.out.println("ALL R: " + cmdTotal.getR() + " / " + zR);
			System.out.println("ALL D: " + cmdTotal.getD() + " / " + zD);
			System.out.println("ALL L: " + cmdTotal.getL() + " / " + zL);
		}
	}

}
