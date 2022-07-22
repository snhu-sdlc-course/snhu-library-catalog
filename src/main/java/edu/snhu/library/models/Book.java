package edu.snhu.library.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("books")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Book {
    @Id
    private Long id;
    private String title;
    private String author;
    private String tags;
}
