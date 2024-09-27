export interface Quiz {
  userId: string;
  quizId: string;
  quizName: string;
  dateCreated: string;
  numTries: number;
  numCorrect: number;
  numQuestions: number;
  metadata: VideoMetadata;
  language: QuizLanguage;
  type: QuizType;
  difficulty: QuizDifficulty;
}

export interface QuizRequest {
  userId: string;
  quizName: string;
  status: RequestStatus;
  dateCreated: string;
  dateFinished: string | null;
  errorMessage: string | null;
  quizId: string | null;
  metadata: RequestMetadata;
}

export interface RequestMetadata {
  videoUrl: string;
  language: QuizLanguage;
  difficulty: QuizDifficulty;
  type: QuizType;
}

export interface QuizCreateRequestDto {
  userId: string;
  quizName: string;
  apiKeys: object;
  videoUrl: string;
  language: QuizLanguage;
  type: QuizType;
  difficulty: QuizDifficulty;
}

export interface QuizUpdateRequestDto {
  userId: string;
  quizId: string;
  quizName: string;
  numCorrect: number;
}

export interface QuizDetails {
  userId: string;
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

export enum RequestStatus {
  ALL = 'ALL',
  CREATING = 'CREATING',
  FINISHED = 'FINISHED',
  FAILED = 'FAILED',
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
  enumValue: RequestStatus | QuizDifficulty | QuizType | QuizLanguage
): string {
  switch (enumValue) {
    // Request status cases
    case RequestStatus.ALL:
      return 'All';
    case RequestStatus.CREATING:
      return 'Creating';
    case RequestStatus.FINISHED:
      return 'Finished';
    case RequestStatus.FAILED:
      return 'Failed';

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
