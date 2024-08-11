export interface Quiz {
  id: string;
  userId: string;
  name: string;
  videoLink: string;
  date: string;
  type: QuizType;
  difficulty: QuizDifficulty;
}

export enum QuizDifficulty {
  EASY = 'Easy',
  MEDIUM = 'Medium',
  HARD = 'Hard',
}

export enum QuizType {
  MULTIPLE_CHOICE = 'Multiple Choice',
}
