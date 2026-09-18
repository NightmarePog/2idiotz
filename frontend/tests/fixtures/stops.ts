import { fakerCS_CZ as faker } from '@faker-js/faker';
import type { Stop } from '../../src/lib/api/generated/types.gen';

export function createStop(id: number, overrides: Partial<Stop> = {}): Stop {
  faker.seed(id);
  return {
    id,
    name: faker.location.street(),
    image_url: null,
    wheelchair_accessible: faker.datatype.boolean(),
    has_shelter: faker.datatype.boolean(),
    has_ticket_machine: faker.datatype.boolean(),
    ...overrides
  };
}
