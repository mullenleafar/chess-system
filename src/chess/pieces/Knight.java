package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class Knight extends ChessPiece {
	public Knight(Board board, Color color) {
		super(board, color);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "N";
	}

	public boolean canMove(Position p) {
		ChessPiece piece = (ChessPiece) getBoard().piece(p);
		return piece == null || piece.getColor() != getColor();
	}

	@Override
	public boolean[][] possibleMoves() {
		boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];
		Position p = new Position(0, 0);

		// 1 pra cima, 2 pra esquerda.
		p.setValues(this.position.getRow() - 1, this.position.getColumn() - 2);
		if (getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}

		// 2 pra cima, 1 pra esquerda.
		p.setValues(this.position.getRow() - 2, this.position.getColumn() - 1);
		if (getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}

		// 1 pra cima, 2 pra direita.
		p.setValues(this.position.getRow() - 1, this.position.getColumn() + 2);
		if (getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}

		// 2 pra cima, 1 pra direita.
		p.setValues(this.position.getRow() - 2, this.position.getColumn() + 1);
		if (getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}

		// 1 pra baixo, 2 pra direita.
		p.setValues(this.position.getRow() + 1, this.position.getColumn() + 2);
		if (getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}

		// 2 pra baixo, 1 pra direita.
		p.setValues(this.position.getRow() + 2, this.position.getColumn() + 1);
		if (getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}

		// 1 pra baixo, 2 pra esquerda.
		p.setValues(this.position.getRow() + 1, this.position.getColumn() - 2);
		if (getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}

		// 2 pra baixo, 1 pra esquerda.
		p.setValues(this.position.getRow() + 2, this.position.getColumn() - 1);
		if (getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}

		return mat;
	}
}
