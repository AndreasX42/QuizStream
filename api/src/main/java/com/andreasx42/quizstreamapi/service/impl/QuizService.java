package com.andreasx42.quizstreamapi.service.impl;

/*
@Service
@AllArgsConstructor
public class QuizService implements IQuizService {

    private final QuizRepository todoRepository;
    private final IUserService userService;
    private final QuizMapper todoMapper;

    public Quiz getById(Long id) {
        Optional<Quiz> todoOptional = todoRepository.findById(id);
        return todoOptional.orElseThrow(() -> new EntityNotFoundException(id, Quiz.class));
    }

    public Page<QuizDto> getAll(Pageable pageable) {
        return todoRepository.findAll(pageable)
                .map(todoMapper::mapFromEntity);
    }

    public QuizDto create(Long userId, QuizDto todoDto) {
        User user = userService.getById(userId);
        Quiz todo = todoMapper.mapToEntity(todoDto);
        todo.setUser(user);

        return todoMapper.mapFromEntity(todoRepository.save(todo));
    }

    public QuizDto update(Long id, QuizDto todoDto) {
        Quiz todoDb = getById(id);
        todoDb.setName(todoDto.name());
        todoDb.setPriority(todoDto.priority());
        todoDb.setStatus(todoDto.status());
        todoDb.setUntilDate(todoDto.untilDate());

        return todoMapper.mapFromEntity(todoRepository.save(todoDb));
    }

    public void delete(Long id) {
        todoRepository.deleteById(id);
    }

    @Override
    public Quiz getByNameAndUserId(String name, Long userId) {
        Optional<Quiz> optionalTodo = todoRepository.findByNameAndUser_Id(name, userId);
        return optionalTodo.orElseThrow(() -> new EntityNotFoundException(name, Quiz.class));
    }

    @Override
    public List<QuizDto> getByUserId(Long userId) {
        return todoRepository.findByUser_Id(userId)
                .stream()
                .map(todoMapper::mapFromEntity)
                .toList();
    }

    public Page<QuizDto> getAllByUserId(Long userId, Pageable pageable) {
        return todoRepository.findAllByUser_Id(userId, pageable)
                .map(todoMapper::mapFromEntity);
    }

}
*/