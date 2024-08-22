export interface Quiz {
  userId: number;
  quizId: string;
  quizName: string;
  dateCreated: string;
  numTries: number;
  numCorrect: number;
  metadata: VideoMetadata;
  type: QuizType;
  difficulty: QuizDifficulty;
}

export interface QuizCreateRequestDto {
  userId: number;
  quizName: string;
  apiKeys: object;
  videoUrl: string;
  type: QuizType;
  difficulty: QuizDifficulty;
}

export interface VideoMetadata {
  title: string;
  videoUrl: string;
  thumbnailUrl: string;
  description: string;
  viewers: number;
  publishDate: string;
  author: string;
}

export enum QuizDifficulty {
  EASY = 'EASY',
  MEDIUM = 'MEDIUM',
  HARD = 'HARD',
}

export enum QuizType {
  MULTIPLE_CHOICE = 'MULTIPLE_CHOICE',
}

export function getEnumDisplayName(enumValue: QuizDifficulty | QuizType): string {
  switch (enumValue) {
    // QuizDifficulty cases
    case QuizDifficulty.EASY:
      return 'Easy';
    case QuizDifficulty.MEDIUM:
      return 'Medium';
    case QuizDifficulty.HARD:
      return 'Hard';

    // QuizType cases
    case QuizType.MULTIPLE_CHOICE:
      return 'Multiple Choice';

    // Default case for unknown values
    default:
      return enumValue;
  }
}
