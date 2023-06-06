package application;

import java.util.InputMismatchException;
import java.util.Scanner;

import chess.ChessException;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;

public class Main {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		ChessMatch match = new ChessMatch();
		
		while(true) {
			try {
				UI.clearScreen();
				UI.printBoard(match.getPieces());
				
				System.out.println();
				System.out.print("Source: ");
				ChessPosition source = UI.readChessPosition(sc);
				
				System.out.println();
				System.out.print("Target: ");
				ChessPosition target = UI.readChessPosition(sc);
				
				ChessPiece captured = match.performChessMove(source, target);
			}
			catch(ChessException | InputMismatchException e) {
				System.out.println(e.getMessage());
				System.out.println("Press ENTER to continue.");
				sc.nextLine();
			}
		}
	}
}
