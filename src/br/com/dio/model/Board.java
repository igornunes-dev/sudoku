package br.com.dio.model;

import java.util.Collection;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.stream.Stream;



public class Board {

    private final List<List<Space>> spaces;

    public Board(final List<List<Space>> spaces) {
        this.spaces = spaces;
    }

    public List<List<Space>> getSpaces() {
        return spaces;
    }

    // Método auxiliar para acessar todos os espaços de forma plana
    private Stream<Space> allSpaces() {
        return spaces.stream().flatMap(Collection::stream);
    }

    public GameStatusEnum getStatus() {
        boolean anyFilled = allSpaces().anyMatch(s -> nonNull(s.getActual()) && !s.isFixed());
        if (!anyFilled) {
            return GameStatusEnum.NON_STARTED;
        }

        boolean anyEmpty = allSpaces().anyMatch(s -> isNull(s.getActual()));
        return anyEmpty ? GameStatusEnum.INCOMPLETE : GameStatusEnum.COMPLETE;
    }

    public boolean hasErrors() {
        if (getStatus() == GameStatusEnum.NON_STARTED) {
            return false;
        }

        return allSpaces().anyMatch(s -> nonNull(s.getActual()) && !s.getActual().equals(s.getExpected()));
    }

    public boolean changeValue(final int col, final int row, final int value) {
        var space = getSpace(col, row);
        if (space.isFixed()) {
            return false;
        }

        space.setActual(value);
        return true;
    }

    public boolean clearValue(final int col, final int row) {
        var space = getSpace(col, row);
        if (space.isFixed()) {
            return false;
        }

        space.clearSpace();
        return true;
    }

    public void reset() {
        allSpaces()
                .filter(s -> !s.isFixed())
                .forEach(Space::clearSpace);
    }

    public boolean isFinished() {
        return !hasErrors() && getStatus() == GameStatusEnum.COMPLETE;
    }

    // Método auxiliar para validar coordenadas e pegar o espaço
    private Space getSpace(final int col, final int row) {
        if (col < 0 || col >= spaces.size() || row < 0 || row >= spaces.get(col).size()) {
            throw new IllegalArgumentException("Invalid coordinates: [" + col + "," + row + "]");
        }
        return spaces.get(col).get(row);
    }
}
