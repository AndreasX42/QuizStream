export interface Quiz {
  userId: number;
  quizId: string;
  quizName: string;
  dateCreated: string;
  numTries: number;
  numCorrect: number;
  metadata: VideoMetadata;
  language: QuizLanguage;
  type: QuizType;
  difficulty: QuizDifficulty;
}

export interface QuizCreateRequestDto {
  userId: number;
  quizName: string;
  apiKeys: object;
  videoUrl: string;
  language: QuizLanguage;
  type: QuizType;
  difficulty: QuizDifficulty;
}

export interface QuizUpdateRequestDto {
  userId: number;
  quizId: string;
  quizName: string;
  numCorrect: number;
}

export interface QuizDetails {
  userId: number;
  quizId: string;
  questionAnswersList: QuizQuestionDetails[];
}

export interface QuizQuestionDetails {
  question: string;
  correctAnswer: string;
  wrongAnswers: string[];
  context: string;
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

export enum QuizLanguage {
  EN = 'EN',
  ES = 'ES',
  DE = 'DE',
}

export function getEnumDisplayName(
  enumValue: QuizDifficulty | QuizType | QuizLanguage
): string {
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

    // QuizLanguage cases
    case QuizLanguage.EN:
      return 'English';
    case QuizLanguage.ES:
      return 'Spanish';
    case QuizLanguage.DE:
      return 'German';

    // Default case for unknown values
    default:
      return enumValue;
  }
}
