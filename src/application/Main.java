package application;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import chess.ChessException;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;

public class Main {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		ChessMatch match = new ChessMatch();
		List<ChessPiece> captured = new ArrayList<>();
		
		while(!match.getCheckmate()) {
			try {
				UI.clearScreen();
				UI.printMatch(match, captured);
				
				System.out.println();
				System.out.print("Source: ");
				ChessPosition source = UI.readChessPosition(sc);
				
				boolean[][] possibleMoves = match.possibleMoves(source);
				UI.clearScreen();
				UI.printBoard(match.getPieces(), possibleMoves);
				
				System.out.println();
				System.out.print("Target: ");
				ChessPosition target = UI.readChessPosition(sc);
				
				ChessPiece capturedPiece = match.performChessMove(source, target);
				if(capturedPiece != null) {
					captured.add(capturedPiece);
				}
				if(match.getPromoted() != null) {
					System.out.print("Enter piece letter for promotion (R/N/B/Q): ");
					String type = sc.nextLine().toUpperCase();
					while(!type.equals("B") && !type.equals("N") && !type.equals("R") && !type.equals("Q")) {
						System.out.print("Invalid value! Enter piece letter for promotion (R/N/B/Q): ");
						type = sc.nextLine().toUpperCase();
					}
					match.replacePromotedPiece(type);
				}
			}
			catch(ChessException | InputMismatchException e) {
				System.out.println(e.getMessage());
				System.out.println("Press ENTER to continue.");
				sc.nextLine();
			}
		}
		
		UI.clearScreen();
		UI.printMatch(match, captured);
	}
}
