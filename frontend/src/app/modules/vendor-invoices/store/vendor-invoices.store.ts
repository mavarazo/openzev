import {
    VendorInvoice,
    VendorInvoicesService,
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
import { Router } from '@angular/router'
import { rxMethod } from '@ngrx/signals/rxjs-interop'
import { filter, map, pipe, switchMap, tap } from 'rxjs'
import { tapResponse } from '@ngrx/operators'
import { HttpErrorResponse } from '@angular/common/http'

type VendorInvoicesState = {
    vendorInvoices: VendorInvoice[]
    vendorInvoice: VendorInvoice | null
}

const initialState: VendorInvoicesState = {
    vendorInvoices: [],
    vendorInvoice: null,
}

export const VendorInvoicesStore = signalStore(
    withState(initialState),
    withRequestStatus(),
    withProps(() => ({
        vendorInvoicesService: inject(VendorInvoicesService),
        router: inject(Router),
    })),
    withMethods((store) => ({
        _loadAll: rxMethod<void>(
            pipe(
                tap(() => patchState(store, setPending())),
                switchMap(() =>
                    store.vendorInvoicesService.getVendorInvoices().pipe(
                        tapResponse({
                            next: (vendorInvoices) =>
                                patchState(
                                    store,
                                    { vendorInvoices: vendorInvoices },
                                    setFulfilled()
                                ),
                            error: (error: HttpErrorResponse) =>
                                patchState(store, setError(error)),
                        })
                    )
                )
            )
        ),

        create: rxMethod<VendorInvoice>(
            pipe(
                tap(() => patchState(store, setPending())),
                switchMap((vendorInvoice) =>
                    store.vendorInvoicesService
                        .createVendorInvoice(vendorInvoice)
                        .pipe(
                            tapResponse({
                                next: (vendorInvoice) => {
                                    patchState(
                                        store,
                                        {
                                            vendorInvoices: [
                                                ...store.vendorInvoices(),
                                                vendorInvoice,
                                            ],
                                        },
                                        setFulfilled()
                                    )
                                    store.router.navigate(['vendor-invoices'])
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
                    store.vendorInvoicesService.getVendorInvoice(id).pipe(
                        tapResponse({
                            next: (vendorInvoice) => {
                                patchState(
                                    store,
                                    { vendorInvoice: vendorInvoice },
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

        change: rxMethod<{ id: string; vendorInvoice: VendorInvoice }>(
            pipe(
                tap(() => patchState(store, setPending())),
                switchMap((command) => {
                    return store.vendorInvoicesService
                        .changeVendorInvoice(command.id, command.vendorInvoice)
                        .pipe(
                            tapResponse({
                                next: (vendorInvoice) => {
                                    const vendorInvoices = [
                                        ...store.vendorInvoices(),
                                    ]
                                    const index = vendorInvoices.findIndex(
                                        (x) => x.id === vendorInvoice.id
                                    )

                                    vendorInvoices[index] = vendorInvoice

                                    patchState(
                                        store,
                                        {
                                            vendorInvoices: vendorInvoices,
                                        },
                                        setFulfilled()
                                    )

                                    store.router.navigate(['vendor-invoices'])
                                },
                                error: (error: HttpErrorResponse) =>
                                    patchState(store, setError(error)),
                            })
                        )
                })
            )
        ),

        remove: rxMethod<VendorInvoice>(
            pipe(
                map((vendorInvoice) => vendorInvoice.id),
                filter(Boolean),
                tap(() => patchState(store, setPending())),
                switchMap((id) =>
                    store.vendorInvoicesService.deleteVendorInvoice(id).pipe(
                        tapResponse({
                            next: () =>
                                patchState(
                                    store,
                                    {
                                        vendorInvoices: [
                                            ...store
                                                .vendorInvoices()
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
