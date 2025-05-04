import {
    Consumption,
    ConsumptionsService,
    MeterPoint,
    MeterPointService,
} from '../../../../generated-source/api'
import {
    patchState,
    signalStore,
    withHooks,
    withMethods,
    withProps,
    withState,
} from '@ngrx/signals'
import {
    setError,
    setFulfilled,
    setPending,
    withRequestStatus,
} from '../../../shared/store/request.feature'
import { inject } from '@angular/core'
import { rxMethod } from '@ngrx/signals/rxjs-interop'
import { filter, map, pipe, switchMap, tap } from 'rxjs'
import { tapResponse } from '@ngrx/operators'
import { HttpErrorResponse } from '@angular/common/http'
import { Router } from '@angular/router'

type ConsumptionsState = {
    consumptions: Consumption[]
    consumption: Consumption | null
    meterPoints: MeterPoint[]
    consumptionsByMeterPoint: Consumption[]
}

const initialState: ConsumptionsState = {
    consumptions: [],
    consumption: null,
    meterPoints: [],
    consumptionsByMeterPoint: [],
}

export const ConsumptionsStore = signalStore(
    withState(initialState),
    withRequestStatus(),
    withProps(() => ({
        consumptionsService: inject(ConsumptionsService),
        meterPointsService: inject(MeterPointService),
        router: inject(Router),
    })),
    withMethods((store) => ({
        _loadAll: rxMethod<void>(
            pipe(
                tap(() => patchState(store, setPending())),
                switchMap(() =>
                    store.consumptionsService.getConsumptions().pipe(
                        tapResponse({
                            next: (consumptions) =>
                                patchState(
                                    store,
                                    { consumptions: consumptions },
                                    setFulfilled()
                                ),
                            error: (error: HttpErrorResponse) =>
                                patchState(store, setError(error)),
                        })
                    )
                )
            )
        ),

        loadMeterPoints: rxMethod<void>(
            pipe(
                tap(() => patchState(store, setPending())),
                switchMap(() =>
                    store.meterPointsService.getMeterPoints().pipe(
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

        loadPreviousConsumptions: rxMethod<string>(
            pipe(
                tap(() => patchState(store, setPending())),
                switchMap((meterPointId) =>
                    store.consumptionsService
                        .getConsumptions(meterPointId)
                        .pipe(
                            tapResponse({
                                next: (consumptions) => {
                                    let result = consumptions
                                    const consumption = store.consumption()
                                    if (consumption) {
                                        result = result.filter(
                                            (c) => c.id !== consumption.id
                                        )
                                    }

                                    patchState(
                                        store,
                                        {
                                            consumptionsByMeterPoint: result,
                                        },
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

        create: rxMethod<Consumption>(
            pipe(
                tap(() => patchState(store, setPending())),
                switchMap((consumption) =>
                    store.consumptionsService
                        .createConsumption(consumption)
                        .pipe(
                            tapResponse({
                                next: (consumption) => {
                                    patchState(
                                        store,
                                        {
                                            consumptions: [
                                                ...store.consumptions(),
                                                consumption,
                                            ],
                                        },
                                        setFulfilled()
                                    )
                                    store.router.navigate(['consumptions'])
                                },
                                error: (error: HttpErrorResponse) =>
                                    patchState(store, setError(error)),
                            })
                        )
                )
            )
        ),

        get: rxMethod<string>(
            pipe(
                tap(() => patchState(store, setPending())),
                switchMap((id) =>
                    store.consumptionsService.getConsumption(id).pipe(
                        tapResponse({
                            next: (consumption) => {
                                patchState(
                                    store,
                                    { consumption: consumption },
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

        change: rxMethod<{ id: string; consumption: Consumption }>(
            pipe(
                tap(() => patchState(store, setPending())),
                switchMap((command) => {
                    return store.consumptionsService
                        .changeConsumption(command.id, command.consumption)
                        .pipe(
                            tapResponse({
                                next: (consumption) => {
                                    const consumptions = [
                                        ...store.consumptions(),
                                    ]
                                    const index = consumptions.findIndex(
                                        (x) => x.id === consumption.id
                                    )

                                    consumptions[index] = consumption

                                    patchState(
                                        store,
                                        {
                                            consumptions: consumptions,
                                        },
                                        setFulfilled()
                                    )

                                    store.router.navigate(['consumptions'])
                                },
                                error: (error: HttpErrorResponse) =>
                                    patchState(store, setError(error)),
                            })
                        )
                })
            )
        ),

        remove: rxMethod<Consumption>(
            pipe(
                map((consumption) => consumption.id),
                filter(Boolean),
                tap(() => patchState(store, setPending())),
                switchMap((id) =>
                    store.consumptionsService.deleteConsumption(id).pipe(
                        tapResponse({
                            next: () =>
                                patchState(
                                    store,
                                    {
                                        consumptions: [
                                            ...store
                                                .consumptions()
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
