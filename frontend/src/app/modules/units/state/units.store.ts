import { Unit, UnitService } from '../../../../generated-source/api'
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
import { withEntities } from '@ngrx/signals/entities'

type UnitsState = {
    units: Unit[]
    unit: Unit | null
}

const initialState: UnitsState = {
    units: [],
    unit: null,
}

export const UnitsStore = signalStore(
    withState(initialState),
    withRequestStatus(),
    withProps(() => ({
        unitService: inject(UnitService),
        router: inject(Router),
    })),
    withEntities<Unit>(),
    withMethods((store) => ({
        _loadAll: rxMethod<void>(
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

        createUnit: rxMethod<Unit>(
            pipe(
                tap(() => patchState(store, setPending())),
                switchMap((unit) =>
                    store.unitService.createUnit(unit).pipe(
                        tapResponse({
                            next: (unit) => {
                                patchState(
                                    store,
                                    {
                                        units: [...store.units(), unit],
                                    },
                                    setFulfilled()
                                )
                                store.router.navigate(['units'])
                            },
                            error: (error: HttpErrorResponse) =>
                                patchState(store, setError(error)),
                        })
                    )
                )
            )
        ),

        getUnit: rxMethod<string>(
            pipe(
                tap(() => patchState(store, setPending())),
                switchMap((id) =>
                    store.unitService.getUnit(id).pipe(
                        tapResponse({
                            next: (unit) => {
                                patchState(
                                    store,
                                    { unit: unit },
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

        changeUnit: rxMethod<{ id: string; unit: Unit }>(
            pipe(
                tap(() => patchState(store, setPending())),
                switchMap((command) => {
                    return store.unitService
                        .changeUnit(command.id, command.unit)
                        .pipe(
                            tapResponse({
                                next: (unit) => {
                                    const units = [...store.units()]
                                    const index = units.findIndex(
                                        (x) => x.id === unit.id
                                    )

                                    units[index] = unit

                                    patchState(
                                        store,
                                        {
                                            units: units,
                                        },
                                        setFulfilled()
                                    )

                                    store.router.navigate(['units'])
                                },
                                error: (error: HttpErrorResponse) =>
                                    patchState(store, setError(error)),
                            })
                        )
                })
            )
        ),

        deleteUnit: rxMethod<Unit>(
            pipe(
                map((unit) => unit.id),
                filter(Boolean),
                tap(() => patchState(store, setPending())),
                switchMap((id) =>
                    store.unitService.deleteUnit(id).pipe(
                        tapResponse({
                            next: () =>
                                patchState(
                                    store,
                                    {
                                        units: [
                                            ...store
                                                .units()
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
