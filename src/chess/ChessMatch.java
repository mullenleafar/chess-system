package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.Bishop;
import chess.pieces.King;
import chess.pieces.Knight;
import chess.pieces.Pawn;
import chess.pieces.Queen;
import chess.pieces.Rook;

public class ChessMatch {
	private Board board;
	private int turn;
	private Color currentPlayer;
	private boolean check;
	private boolean checkmate;
	private ChessPiece enPassantVulnerable;
	private ChessPiece promoted;

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

	public ChessPiece getEnPassantVulnerable() {
		return this.enPassantVulnerable;
	}
	
	public ChessPiece getPromoted() {
		return this.promoted;
	}

	public ChessPiece[][] getPieces() {
		ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];

		for (int i = 0; i < board.getRows(); i++) {
			for (int j = 0; j < board.getColumns(); j++) {
				mat[i][j] = (ChessPiece) board.piece(i, j);
			}
		}
		return mat;
	}

	private void initialSetup() {
		placeNewPiece('a', 8, new Rook(this.board, Color.BLACK));
		placeNewPiece('b', 8, new Knight(this.board, Color.BLACK));
		placeNewPiece('c', 8, new Bishop(this.board, Color.BLACK));
		placeNewPiece('d', 8, new Queen(this.board, Color.BLACK));
		placeNewPiece('e', 8, new King(this.board, Color.BLACK, this));
		placeNewPiece('f', 8, new Bishop(this.board, Color.BLACK));
		placeNewPiece('g', 8, new Knight(this.board, Color.BLACK));
		placeNewPiece('h', 8, new Rook(this.board, Color.BLACK));
		placeNewPiece('a', 7, new Pawn(this.board, Color.BLACK, this));
		placeNewPiece('b', 7, new Pawn(this.board, Color.BLACK, this));
		placeNewPiece('c', 7, new Pawn(this.board, Color.BLACK, this));
		placeNewPiece('d', 7, new Pawn(this.board, Color.BLACK, this));
		placeNewPiece('e', 7, new Pawn(this.board, Color.BLACK, this));
		placeNewPiece('f', 7, new Pawn(this.board, Color.BLACK, this));
		placeNewPiece('g', 7, new Pawn(this.board, Color.BLACK, this));
		placeNewPiece('h', 7, new Pawn(this.board, Color.BLACK, this));

		placeNewPiece('a', 1, new Rook(this.board, Color.WHITE));
		placeNewPiece('b', 1, new Knight(this.board, Color.WHITE));
		placeNewPiece('c', 1, new Bishop(this.board, Color.WHITE));
		placeNewPiece('d', 1, new Queen(this.board, Color.WHITE));
		placeNewPiece('e', 1, new King(this.board, Color.WHITE, this));
		placeNewPiece('f', 1, new Bishop(this.board, Color.WHITE));
		placeNewPiece('g', 1, new Knight(this.board, Color.WHITE));
		placeNewPiece('h', 1, new Rook(this.board, Color.WHITE));
		placeNewPiece('a', 2, new Pawn(this.board, Color.WHITE, this));
		placeNewPiece('b', 2, new Pawn(this.board, Color.WHITE, this));
		placeNewPiece('c', 2, new Pawn(this.board, Color.WHITE, this));
		placeNewPiece('d', 2, new Pawn(this.board, Color.WHITE, this));
		placeNewPiece('e', 2, new Pawn(this.board, Color.WHITE, this));
		placeNewPiece('f', 2, new Pawn(this.board, Color.WHITE, this));
		placeNewPiece('g', 2, new Pawn(this.board, Color.WHITE, this));
		placeNewPiece('h', 2, new Pawn(this.board, Color.WHITE, this));
	}

	public void placeNewPiece(char column, int row, ChessPiece chessPiece) {
		board.placePiece(chessPiece, new ChessPosition(column, row).toPosition());
		this.piecesOnBoard.add(chessPiece);
	}

	public boolean[][] possibleMoves(ChessPosition source) {
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

		if (testCheck(currentPlayer)) {
			undoMove(source, target, capturedPiece);
			throw new ChessException("You can't put yourself in check.");
		}

		ChessPiece movedPiece = (ChessPiece) this.board.piece(target);
		
		//Promoção
		this.promoted = null;
		if(movedPiece instanceof Pawn) {
			if((movedPiece.getColor() == Color.WHITE && target.getRow() == 0) || movedPiece.getColor() == Color.BLACK && target.getRow() == 7) {
				promoted = (ChessPiece)this.board.piece(target);
				promoted = replacePromotedPiece("Q");
			}
		}

		check = testCheck(opponent(this.currentPlayer)) ? true : false;

		if (testCheckmate(opponent(this.currentPlayer))) {
			this.checkmate = true;
		} else {
			nextTurn();
		}

		// En passant
		if (movedPiece instanceof Pawn
				&& (target.getRow() == source.getRow() + 2 || target.getRow() == source.getRow() - 2)) {
			this.enPassantVulnerable = movedPiece;
		} else {
			this.enPassantVulnerable = null;
		}

		return (ChessPiece) capturedPiece;
	}
	
	public ChessPiece replacePromotedPiece(String type) {
		if(promoted == null) {
			throw new IllegalStateException("There's no piece to be promoted.");
		}
		if(!type.equals("B") && !type.equals("N") && !type.equals("R") && !type.equals("Q")) {
			return promoted;
		}
		Position pos = promoted.getChessPosition().toPosition();
		Piece p = this.board.removePiece(pos);
		this.piecesOnBoard.remove(p);
		
		ChessPiece newPiece = newPiece(type, promoted.getColor());
		this.board.placePiece(newPiece, pos);
		this.piecesOnBoard.add(newPiece);
		return newPiece;
	}
	
	private ChessPiece newPiece(String type, Color color) {
		if(type.equals("B")) {
			return new Bishop(board, color);
		}
		if(type.equals("N")) {
			return new Knight(board, color);
		}
		if(type.equals("Q")) {
			return new Queen(board, color);
		}
		else {
			return new Rook(board, color);
		}
	}

	private Piece makeMove(Position source, Position target) {
		ChessPiece piece = (ChessPiece) board.removePiece(source);
		piece.increaseMoveCount();
		Piece captured = board.removePiece(target);
		board.placePiece(piece, target);
		if (captured != null) {
			this.piecesOnBoard.remove(captured);
			this.capturedPieces.add(captured);
		}

		// Roque pequeno
		if (piece instanceof King && target.getColumn() == source.getColumn() + 2) {
			Position sourceR = new Position(source.getRow(), source.getColumn() + 3);
			Position targetR = new Position(source.getRow(), source.getColumn() + 1);
			ChessPiece rook = (ChessPiece) board.removePiece(sourceR);
			board.placePiece(rook, targetR);
			rook.increaseMoveCount();
		}

		// Roque grande
		if (piece instanceof King && target.getColumn() == source.getColumn() - 2) {
			Position sourceR = new Position(source.getRow(), source.getColumn() - 4);
			Position targetR = new Position(source.getRow(), source.getColumn() - 1);
			ChessPiece rook = (ChessPiece) board.removePiece(sourceR);
			board.placePiece(rook, targetR);
			rook.increaseMoveCount();
		}

		// En passant
		if (piece instanceof Pawn) {
			if (source.getColumn() != target.getColumn() && captured == null) {
				Position pawnPosition;
				if (piece.getColor() == Color.WHITE) {
					pawnPosition = new Position(target.getRow() + 1, target.getColumn());
				} else {
					pawnPosition = new Position(target.getRow() - 1, target.getColumn());
				}
				captured = this.board.removePiece(pawnPosition);
				capturedPieces.add(captured);
				piecesOnBoard.remove(captured);
			}
		}
		return captured;
	}

	private void undoMove(Position source, Position target, Piece capturedPiece) {
		ChessPiece piece = (ChessPiece) board.removePiece(target);
		piece.decreaseMoveCount();
		board.placePiece(piece, source);

		if (capturedPiece != null) {
			board.placePiece(capturedPiece, target);
			capturedPieces.remove(capturedPiece);
			piecesOnBoard.add(capturedPiece);
		}

		// Roque pequeno
		if (piece instanceof King && target.getColumn() == source.getColumn() + 2) {
			Position sourceR = new Position(source.getRow(), source.getColumn() + 3);
			Position targetR = new Position(source.getRow(), source.getColumn() + 1);
			ChessPiece rook = (ChessPiece) board.removePiece(targetR);
			board.placePiece(rook, sourceR);
			rook.decreaseMoveCount();
		}

		// Roque grande
		if (piece instanceof King && target.getColumn() == source.getColumn() - 2) {
			Position sourceR = new Position(source.getRow(), source.getColumn() - 4);
			Position targetR = new Position(source.getRow(), source.getColumn() - 1);
			ChessPiece rook = (ChessPiece) board.removePiece(targetR);
			board.placePiece(rook, sourceR);
			rook.decreaseMoveCount();
		}

		// En passant
		if (piece instanceof Pawn) {
			if (source.getColumn() != target.getColumn() && capturedPiece == this.enPassantVulnerable) {
				ChessPiece pawn = (ChessPiece)this.board.removePiece(target);
				Position pawnPosition;
				if (piece.getColor() == Color.WHITE) {
					pawnPosition = new Position(3, target.getColumn());
				} else {
					pawnPosition = new Position(4, target.getColumn());
				}
				this.board.placePiece(pawn, pawnPosition);
			}
		}
	}

	private void validateSourcePosition(Position position) {
		if (!board.thereIsAPiece(position)) {
			throw new ChessException("There is no piece on source position.");
		}
		if (currentPlayer != ((ChessPiece) this.board.piece(position)).getColor()) {
			throw new ChessException("Chosen piece isn't yours.");
		}
		if (!board.piece(position).isThereAnyPossibleMove()) {
			throw new ChessException("No moves available for the chosen piece.");
		}
	}

	private void validateTargetPosition(Position source, Position target) {
		if (!board.piece(source).possibleMove(target)) {
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
		List<Piece> list = piecesOnBoard.stream().filter(x -> ((ChessPiece) x).getColor() == color)
				.collect(Collectors.toList());
		for (Piece p : list) {
			if (p instanceof King) {
				return (ChessPiece) p;
			}
		}
		throw new IllegalStateException("There is no " + color + " king on the board.");
	}

	private boolean testCheck(Color color) {
		Position kingPosition = king(color).getChessPosition().toPosition();
		List<Piece> opponentPieces = piecesOnBoard.stream().filter(x -> ((ChessPiece) x).getColor() == opponent(color))
				.collect(Collectors.toList());
		for (Piece p : opponentPieces) {
			boolean[][] mat = p.possibleMoves();
			if (mat[kingPosition.getRow()][kingPosition.getColumn()]) {
				return true;
			}
		}
		return false;
	}

	private boolean testCheckmate(Color color) {
		if (!testCheck(color)) {
			return false;
		}
		List<Piece> list = piecesOnBoard.stream().filter(x -> ((ChessPiece) x).getColor() == color)
				.collect(Collectors.toList());
		for (Piece p : list) {
			boolean[][] mat = p.possibleMoves();
			for (int i = 0; i < this.board.getRows(); i++) {
				for (int j = 0; j < this.board.getColumns(); j++) {
					if (mat[i][j]) {
						Position source = ((ChessPiece) p).getChessPosition().toPosition();
						Position target = new Position(i, j);
						Piece capturedPiece = makeMove(source, target);
						boolean testCheck = testCheck(color);
						undoMove(source, target, capturedPiece);
						if (!testCheck) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

}
