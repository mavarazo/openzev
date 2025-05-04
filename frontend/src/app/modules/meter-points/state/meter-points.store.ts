import {
    patchState,
    signalStore,
    withHooks,
    withMethods,
    withProps,
    withState,
} from '@ngrx/signals'
import {
    MeterPoint,
    MeterPointService,
    Unit,
    UnitService,
} from '../../../../generated-source/api'
import {
    setError,
    setFulfilled,
    setPending,
    withRequestStatus,
} from '../../../shared/store/request.feature'
import { inject } from '@angular/core'
import { rxMethod } from '@ngrx/signals/rxjs-interop'
import { tapResponse } from '@ngrx/operators'
import { filter, map, pipe, switchMap, tap } from 'rxjs'
import { HttpErrorResponse } from '@angular/common/http'
import { Router } from '@angular/router'

type MeterPointsState = {
    meterPoints: MeterPoint[]
    meterPoint: MeterPoint | null
    units: Unit[]
}

const initialState: MeterPointsState = {
    meterPoints: [],
    meterPoint: null,
    units: [],
}

export const MeterPointsStore = signalStore(
    withState(initialState),
    withRequestStatus(),
    withProps(() => ({
        meterPointService: inject(MeterPointService),
        unitService: inject(UnitService),
        router: inject(Router),
    })),
    withMethods((store) => ({
        _loadAll: rxMethod<void>(
            pipe(
                tap(() => patchState(store, setPending())),
                switchMap(() =>
                    store.meterPointService.getMeterPoints().pipe(
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

        loadUnits: rxMethod<void>(
            pipe(
                tap(() => patchState(store, setPending())),
                switchMap(() =>
                    store.unitService.getUnits().pipe(
                        tapResponse({
                            next: (units) =>
                                patchState(
                                    store,
                                    { units: units },
                                    setFulfilled()
                                ),
                            error: (error: HttpErrorResponse) =>
                                patchState(store, setError(error)),
                        })
                    )
                )
            )
        ),

        createMeterPoint: rxMethod<MeterPoint>(
            pipe(
                tap(() => patchState(store, setPending())),
                switchMap((meterPoint) =>
                    store.meterPointService.createMeterPoint(meterPoint).pipe(
                        tapResponse({
                            next: (meterPoint) => {
                                patchState(
                                    store,
                                    {
                                        meterPoints: [
                                            ...store.meterPoints(),
                                            meterPoint,
                                        ],
                                    },
                                    setFulfilled()
                                )
                                store.router.navigate(['meter-points'])
                            },
                            error: (error: HttpErrorResponse) =>
                                patchState(store, setError(error)),
                        })
                    )
                )
            )
        ),

        getMeterPoint: rxMethod<string>(
            pipe(
                tap(() => patchState(store, setPending())),
                switchMap((id) =>
                    store.meterPointService.getMeterPoint(id).pipe(
                        tapResponse({
                            next: (meterPoint) => {
                                patchState(
                                    store,
                                    { meterPoint: meterPoint },
                                    setFulfilled()
                                )
                            },
                            error: (error: HttpErrorResponse) =>
                                patchState(store, setError(error)),
                        })
                    )
                )
            )
        ),

        changeMeterPoint: rxMethod<{ id: string; meterPoint: MeterPoint }>(
            pipe(
                tap(() => patchState(store, setPending())),
                switchMap((command) => {
                    return store.meterPointService
                        .changeMeterPoint(command.id, command.meterPoint)
                        .pipe(
                            tapResponse({
                                next: (meterPoint) => {
                                    const meterPoints = [...store.meterPoints()]
                                    const index = meterPoints.findIndex(
                                        (x) => x.id === meterPoint.id
                                    )

                                    meterPoints[index] = meterPoint

                                    patchState(
                                        store,
                                        {
                                            meterPoints: meterPoints,
                                        },
                                        setFulfilled()
                                    )

                                    store.router.navigate(['meter-points'])
                                },
                                error: (error: HttpErrorResponse) =>
                                    patchState(store, setError(error)),
                            })
                        )
                })
            )
        ),

        removeMeterPoint: rxMethod<MeterPoint>(
            pipe(
                map((meterPoint) => meterPoint.id),
                filter(Boolean),
                tap(() => patchState(store, setPending())),
                switchMap((id) =>
                    store.meterPointService.deleteMeterPoint(id).pipe(
                        tapResponse({
                            next: () =>
                                patchState(
                                    store,
                                    {
                                        meterPoints: [
                                            ...store
                                                .meterPoints()
                                                .filter((x) => x.id !== id),
                                        ],
                                    },
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
