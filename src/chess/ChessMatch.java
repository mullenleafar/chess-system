package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.King;
import chess.pieces.Rook;

public class ChessMatch {
	private Board board;
	private int turn;
	private Color currentPlayer;
	private boolean check;
	private boolean checkmate;
	
	private List<Piece> piecesOnBoard;
	private List<Piece> capturedPieces;
	
	public ChessMatch() {
		this.board = new Board(8, 8);
		this.turn = 1;
		this.currentPlayer = Color.WHITE;
		this.piecesOnBoard = new ArrayList<>();
		this.capturedPieces = new ArrayList<>();
		initialSetup();
	}
	
	public int getTurn() {
		return this.turn;
	}
	
	public Color getCurrentPlayer() {
		return this.currentPlayer;
	}
	
	public boolean getCheck() {
		return this.check;
	}
	
	public boolean getCheckmate() {
		return this.checkmate;
	}

	public ChessPiece[][] getPieces(){
		ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
		
		for(int i = 0; i < board.getRows(); i++) {
			for(int j = 0; j < board.getColumns(); j++) {
				mat[i][j] = (ChessPiece) board.piece(i, j);
			}
		}
		return mat;
	}
	
	private void initialSetup() {
		placeNewPiece('h', 7, new Rook(this.board, Color.WHITE));
		placeNewPiece('d', 1, new Rook(this.board, Color.WHITE));
		placeNewPiece('e', 1, new King(this.board, Color.WHITE));
		placeNewPiece('b', 8, new Rook(this.board, Color.BLACK));
		placeNewPiece('a', 8, new King(this.board, Color.BLACK));
	}
	
	public void placeNewPiece(char column, int row, ChessPiece chessPiece) {
		board.placePiece(chessPiece, new ChessPosition(column, row).toPosition());
		this.piecesOnBoard.add(chessPiece);
	}
	
	public boolean[][] possibleMoves(ChessPosition source){
		Position p = source.toPosition();
		validateSourcePosition(p);
		return this.board.piece(p).possibleMoves();
	}
	
	public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();
		validateSourcePosition(source);
		validateTargetPosition(source, target);
		Piece capturedPiece = makeMove(source, target);
		
		if(testCheck(currentPlayer)) {
			undoMove(source, target, capturedPiece);
			throw new ChessException("You can't put yourself in check.");
		}
		
		check = testCheck(opponent(this.currentPlayer)) ? true : false;
		
		if(testCheckmate(opponent(this.currentPlayer))) {
			this.checkmate = true;
		}else {
			nextTurn();
		}
		return (ChessPiece)capturedPiece;
	}
	
	private Piece makeMove(Position source, Position target) {
		ChessPiece piece = (ChessPiece)board.removePiece(source);
		piece.increaseMoveCount();
		Piece captured = board.removePiece(target);
		board.placePiece(piece, target);
		if(captured != null) {
			this.piecesOnBoard.remove(captured);
			this.capturedPieces.add(captured);
		}
		return captured;
	}
	
	private void undoMove(Position source, Position target, Piece capturedPiece) {
		ChessPiece piece = (ChessPiece)board.removePiece(target);
		piece.decreaseMoveCount();
		board.placePiece(piece, source);
		
		if(capturedPiece != null) {
			board.placePiece(capturedPiece, target);
			capturedPieces.remove(capturedPiece);
			piecesOnBoard.add(capturedPiece);
		}
	}
	
	private void validateSourcePosition(Position position) {
		if(!board.thereIsAPiece(position)) {
			throw new ChessException("There is no piece on source position.");
		}
		if(currentPlayer != ((ChessPiece)this.board.piece(position)).getColor()) {
			throw new ChessException("Chosen piece isn't yours.");
		}
		if(!board.piece(position).isThereAnyPossibleMove()) {
			throw new ChessException("No moves available for the chosen piece.");
		}
	}
	
	private void validateTargetPosition(Position source, Position target) {
		if(!board.piece(source).possibleMove(target)) {
			throw new ChessException("The chosen piece can't move to the targeted position.");
		}
	}
	
	private void nextTurn() {
		this.turn++;
		this.currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}
	
	private Color opponent(Color color) {
		return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}
	
	private ChessPiece king(Color color) {
		List<Piece> list = piecesOnBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
		for(Piece p : list) {
			if(p instanceof King) {
				return (ChessPiece)p;
			}
		}
		throw new IllegalStateException("There is no " + color + " king on the board.");
	}
	
	private boolean testCheck(Color color) {
		Position kingPosition = king(color).getChessPosition().toPosition();
		List<Piece> opponentPieces = piecesOnBoard.stream().filter(x -> ((ChessPiece)x).getColor() == opponent(color)).collect(Collectors.toList());
		for(Piece p : opponentPieces) {
			boolean[][] mat = p.possibleMoves();
			if(mat[kingPosition.getRow()][kingPosition.getColumn()]) {
				return true;
			}
		}
		return false;
	}
	
	private boolean testCheckmate(Color color) {
		if(!testCheck(color)) {
			return false;
		}
		List<Piece> list = piecesOnBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
		for(Piece p : list) {
			boolean[][] mat = p.possibleMoves();
			for(int i = 0; i < this.board.getRows(); i++) {
				for(int j = 0; j < this.board.getColumns(); j++) {
					if(mat[i][j]) {
						Position source = ((ChessPiece)p).getChessPosition().toPosition();
						Position target = new Position(i, j);
						Piece capturedPiece = makeMove(source, target);
						boolean testCheck = testCheck(color);
						undoMove(source, target, capturedPiece);
						if(!testCheck) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	
	
	
}
