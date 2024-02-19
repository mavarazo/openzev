import { AnyAccounting, ZevAccountingDto } from '../../generated-source/api';

export default class Utils {
  static isZevAccounting(
    accounting: AnyAccounting
  ): accounting is ZevAccountingDto {
    return (<ZevAccountingDto>accounting).agreementId !== undefined;
  }
}
