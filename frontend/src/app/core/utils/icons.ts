export interface IconOption {
  id: number;
  icon: string;
  label: string;
}

export const RESOURCE_ICONS: IconOption[] = [
  { id: 1,  icon: 'lucide:box',           label: 'Коробка' },
  { id: 2,  icon: 'lucide:monitor',        label: 'Монитор' },
  { id: 3,  icon: 'lucide:laptop',         label: 'Ноутбук' },
  { id: 4,  icon: 'lucide:users',          label: 'Переговорная' },
  { id: 5,  icon: 'lucide:door-open',      label: 'Комната' },
  { id: 6,  icon: 'lucide:camera',         label: 'Студия' },
  { id: 7,  icon: 'lucide:mic',            label: 'Микрофон' },
  { id: 8,  icon: 'lucide:car',            label: 'Автомобиль' },
  { id: 9,  icon: 'lucide:bike',           label: 'Велосипед' },
  { id: 10, icon: 'lucide:wrench',         label: 'Инструмент' },
  { id: 11, icon: 'lucide:printer',        label: 'Принтер' },
  { id: 12, icon: 'lucide:tv',             label: 'Телевизор' },
  { id: 13, icon: 'lucide:coffee',         label: 'Кофемашина' },
  { id: 14, icon: 'lucide:wifi',           label: 'Точка доступа' },
  { id: 15, icon: 'lucide:phone',          label: 'Телефон' },
  { id: 16, icon: 'lucide:gamepad-2',      label: 'Игровое' },
  { id: 17, icon: 'lucide:dumbbell',       label: 'Спортзал' },
  { id: 18, icon: 'lucide:building-2',     label: 'Здание' },
  { id: 19, icon: 'lucide:armchair',       label: 'Кресло' },
  { id: 20, icon: 'lucide:utensils',       label: 'Кухня' },
  { id: 21, icon: 'lucide:flask-conical',  label: 'Лаборатория' },
  { id: 22, icon: 'lucide:server',         label: 'Сервер' },
  { id: 23, icon: 'lucide:hard-drive',     label: 'Хранилище' },
  { id: 24, icon: 'lucide:projector',      label: 'Проектор' },
  { id: 25, icon: 'lucide:warehouse',      label: 'Склад' },
];

export function getIconById(id: number | null | undefined): string {
  if (!id) return 'lucide:box';
  return RESOURCE_ICONS.find(i => i.id === id)?.icon || 'lucide:box';
}
