export interface Quiz {
  id: string;
  userId: number;
  name: string;
  videoLink: string;
  dateCreated: string;
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
