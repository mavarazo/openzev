export * from './meterPoint.service';
import { MeterPointService } from './meterPoint.service';
export * from './reading.service';
import { ReadingService } from './reading.service';
export const APIS = [MeterPointService, ReadingService];
