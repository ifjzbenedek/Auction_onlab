export interface TimeDisplayProps {
  expiredDate: string;
  startDate?: string | null;
  variant?: 'compact' | 'detailed' | 'chip';
  showIcon?: boolean;
  color?: 'primary' | 'secondary' | 'error' | 'warning';
  size?: 'small' | 'medium' | 'large';
}