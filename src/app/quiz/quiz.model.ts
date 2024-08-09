export interface Quiz {
  id: string;
  userId: string;
  name: string;
  videoLink: string;
  date: string;
  type: QuizType;
  difficulty: QuizDifficulty;
}

export type QuizType = 'MULTIPLE_CHOICE' | 'TEXT_BASED';

export type QuizDifficulty = 'EASY' | 'MEDIUM' | 'HARD';
