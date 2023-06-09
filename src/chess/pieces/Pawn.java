package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;

public class Pawn extends ChessPiece {

	private ChessMatch chessMatch;

	public Pawn(Board board, Color color, ChessMatch chessMatch) {
		super(board, color);
		this.chessMatch = chessMatch;
	}

	@Override
	public String toString() {
		return "P";
	}

	@Override
	public boolean[][] possibleMoves() {
		boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];
		Position p = new Position(0, 0);

		// BRANCO
		if (this.getColor() == Color.WHITE) {

			// 1 para cima (somente se estiver desocupada).
			p.setValues(this.position.getRow() - 1, this.position.getColumn());
			if (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}

			// Se for o primeiro movimento, 2 para cima (somente se estiver desocupada).
			p.setValues(this.position.getRow() - 2, this.position.getColumn());
			Position p2 = new Position(this.position.getRow() - 1, this.position.getColumn());
			if (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p) && this.getMoveCount() == 0
					&& getBoard().positionExists(p2) && !getBoard().thereIsAPiece(p2)) {
				mat[p.getRow()][p.getColumn()] = true;
			}

			// 1 para cima-esquerda (somente se for capturar uma peça adversária).
			p.setValues(this.position.getRow() - 1, this.position.getColumn() - 1);
			if (getBoard().positionExists(p) && isThereOpponentPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}

			// 1 para cima-direita (somente se for capturar uma peça adversária).
			p.setValues(this.position.getRow() - 1, this.position.getColumn() + 1);
			if (getBoard().positionExists(p) && isThereOpponentPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}

			// En passant
			if (position.getRow() == 3) {
				Position left = new Position(position.getRow(), position.getColumn() - 1);
				if (this.getBoard().positionExists(left) && isThereOpponentPiece(left)
						&& this.getBoard().piece(left) == chessMatch.getEnPassantVulnerable()) {
					mat[left.getRow() - 1][left.getColumn()] = true;
				}
				Position right = new Position(position.getRow(), position.getColumn() + 1);
				if (this.getBoard().positionExists(right) && isThereOpponentPiece(right)
						&& this.getBoard().piece(right) == chessMatch.getEnPassantVulnerable()) {
					mat[right.getRow() - 1][right.getColumn()] = true;
				}
			}

		}

		// PRETO
		else {
			// 1 para baixo (somente se estiver desocupada).
			p.setValues(this.position.getRow() + 1, this.position.getColumn());
			if (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}

			// Se for o primeiro movimento, 2 para baixo (somente se estiver desocupada).
			p.setValues(this.position.getRow() + 2, this.position.getColumn());
			Position p2 = new Position(this.position.getRow() + 1, this.position.getColumn());
			if (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p) && this.getMoveCount() == 0
					&& getBoard().positionExists(p2) && !getBoard().thereIsAPiece(p2)) {
				mat[p.getRow()][p.getColumn()] = true;
			}

			// 1 para baixo-esquerda (somente se for capturar uma peça adversária).
			p.setValues(this.position.getRow() + 1, this.position.getColumn() - 1);
			if (getBoard().positionExists(p) && isThereOpponentPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}

			// 1 para baixo-direita (somente se for capturar uma peça adversária).
			p.setValues(this.position.getRow() + 1, this.position.getColumn() + 1);
			if (getBoard().positionExists(p) && isThereOpponentPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}

			// En passant
			if (position.getRow() == 4) {
				Position left = new Position(position.getRow(), position.getColumn() - 1);
				if (this.getBoard().positionExists(left) && isThereOpponentPiece(left)
						&& this.getBoard().piece(left) == chessMatch.getEnPassantVulnerable()) {
					mat[left.getRow() + 1][left.getColumn()] = true;
				}
				Position right = new Position(position.getRow(), position.getColumn() + 1);
				if (this.getBoard().positionExists(right) && isThereOpponentPiece(right)
						&& this.getBoard().piece(right) == chessMatch.getEnPassantVulnerable()) {
					mat[right.getRow() + 1][right.getColumn()] = true;
				}
			}

		}
		return mat;
	}

}
