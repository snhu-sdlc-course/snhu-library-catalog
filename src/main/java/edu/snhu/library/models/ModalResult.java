package edu.snhu.library.models;

import edu.snhu.library.enums.ModalResultStatus;

public record ModalResult<P>(ModalResultStatus status, P payload) {
}
