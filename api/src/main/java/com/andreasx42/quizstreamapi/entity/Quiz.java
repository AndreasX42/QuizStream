package com.andreasx42.quizstreamapi.entity;

/*
@Entity
@Table(name = "todos")
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class Quiz {

    public enum Status {
        OPEN, FINISHED
    }

    public enum Priority {
        LOW, MID, HIGH
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nonnull
    @NotBlank(message = "name cannot be blank")
    @Column(nullable = false, unique = false)
    private String name;

    @Nonnull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;

    @Nonnull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Nonnull
    @Future(message = "The deadline must be in the future")
    @Column(nullable = false)
    // @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate untilDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
*/