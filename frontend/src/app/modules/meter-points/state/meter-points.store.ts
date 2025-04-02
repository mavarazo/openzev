import {
    patchState,
    signalStore,
    withHooks,
    withMethods,
    withState,
} from '@ngrx/signals'
import { MeterPoint, MeterPointService } from '../../../../generated-source/api'
import {
    setError,
    setFulfilled,
    setPending,
    withRequestStatus,
} from '../../../shared/store/request.feature'
import { inject } from '@angular/core'
import { rxMethod } from '@ngrx/signals/rxjs-interop'
import { tapResponse } from '@ngrx/operators'
import { pipe, switchMap, tap } from 'rxjs'
import { HttpErrorResponse } from '@angular/common/http'

type MeterPointsState = {
    meterPoints: MeterPoint[]
    meterPoint?: MeterPoint
}

const initialState: MeterPointsState = {
    meterPoints: [],
    meterPoint: undefined,
}

export const MeterPointsStore = signalStore(
    withState(initialState),
    withRequestStatus(),
    withMethods((store, meterPointService = inject(MeterPointService)) => ({
        _loadAll: rxMethod<void>(
            pipe(
                tap(() => patchState(store, setPending())),
                switchMap(() =>
                    meterPointService.getMeterPoints().pipe(
                        tapResponse({
                            next: (meterPoints) =>
                                patchState(
                                    store,
                                    { meterPoints: meterPoints },
                                    setFulfilled()
                                ),
                            error: (error: HttpErrorResponse) =>
                                patchState(store, setError(error)),
                        })
                    )
                )
            )
        ),
    })),
    withHooks({
        onInit(store) {
            store._loadAll()
        },
    })
)
